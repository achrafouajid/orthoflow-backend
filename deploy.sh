#!/usr/bin/env bash
# OrthoFlow production deploy, invoked over SSH by a GitHub Actions deploy key
# (webfactory/ssh-agent -> ssh bento@… "…/deploy.sh <service> <tag>").
#
#   deploy.sh <service> <image-tag>
#     service    : backend | front
#     image-tag  : the :<sha> tag GHCR just built for that service
#
# Both services share one compose file (this repo's root); the frontend lives
# in its own repo and ships as its own image. A deploy touches only the named
# service — CI pushes :latest alongside :<sha>, and `docker compose up -d`
# recreates only a service whose image digest actually changed, so a backend
# deploy leaves the running frontend (and Postgres) alone, and vice versa.
#
# Env in:  GHCR_LOGIN_USER / GHCR_LOGIN_TOKEN  -> docker login ghcr.io
#          DEPLOY_COMPOSE_FILE                  -> compose file (default below)
#
# Non-zero exit fails the Actions job. The previous container keeps running if
# the new image never becomes healthy.
set -euo pipefail

APP_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
COMPOSE_FILE="${DEPLOY_COMPOSE_FILE:-docker-compose.yml}"
PROJECT="$(basename "$APP_DIR")"
SERVICE="${1:?usage: deploy.sh <backend|front> <image-tag>}"
IMAGE_TAG="${2:?usage: deploy.sh <backend|front> <image-tag>}"

case "$SERVICE" in
  backend) TAG_VAR=BACKEND_IMAGE_TAG ;;
  front)   TAG_VAR=FRONT_IMAGE_TAG ;;
  *) echo "ERROR: service must be 'backend' or 'front', got '$SERVICE'" >&2; exit 2 ;;
esac

cd "$APP_DIR"
dc() { docker compose -p "$PROJECT" -f "$COMPOSE_FILE" "$@"; }

# Keep the compose file and this script in step with the repo before acting on
# them. --ff-only fails loudly rather than clobbering a hand edit on the box.
# orthoflow-backend is a PUBLIC repo, so the fetch needs no credentials:
#   - http.version=HTTP/1.1 dodges the HTTP/2-multiplexing 401 that git 2.43 /
#     curl hit on GitHub for an anonymous upload-pack POST;
#   - GIT_TERMINAL_PROMPT=0 fails fast instead of prompting for a username;
#   - cleared credential.helper / extraheader ignore any stale cached token
#     that would turn the anonymous 200 into a 401.
# Do NOT reuse this pattern if the repo is ever made private.
git_pub() {
  GIT_TERMINAL_PROMPT=0 git \
    -c http.version=HTTP/1.1 \
    -c credential.helper= \
    -c 'http.https://github.com/.extraheader=' \
    "$@"
}
BRANCH="$(git rev-parse --abbrev-ref HEAD)"
git_pub fetch --quiet origin "$BRANCH"
git_pub merge --ff-only --quiet "origin/$BRANCH"

if [ -n "${GHCR_LOGIN_TOKEN:-}" ]; then
  echo "$GHCR_LOGIN_TOKEN" | docker login ghcr.io -u "${GHCR_LOGIN_USER:-github-actions}" --password-stdin
fi

export "$TAG_VAR=$IMAGE_TAG"
dc pull "$SERVICE"

# Pin the service we are NOT deploying to the image it is already running.
#
# Without this, deploying one service silently rolls the other back. The tags
# in docker-compose.yml default to `latest` (`${BACKEND_IMAGE_TAG:-latest}`),
# and only the deployed service's tag variable is exported — so the `up -d`
# below re-resolves the sibling from `:latest`, which is whatever stale copy
# happens to be in the local image cache. `dc pull` only ever fetches the
# service being deployed, so that cached `:latest` can be many deploys old.
#
# This bit for real: a frontend deploy reverted the backend to a build from
# three hours earlier, and nothing failed — the container came up healthy on
# the wrong code, and the deploy reported success.
OTHER_SERVICE=$([ "$SERVICE" = backend ] && echo front || echo backend)
OTHER_TAG_VAR=$([ "$SERVICE" = backend ] && echo FRONT_IMAGE_TAG || echo BACKEND_IMAGE_TAG)
OTHER_CONTAINER="$(dc ps -q "$OTHER_SERVICE" 2>/dev/null || true)"
if [ -n "$OTHER_CONTAINER" ]; then
  OTHER_TAG="$(docker inspect -f '{{.Config.Image}}' "$OTHER_CONTAINER" 2>/dev/null | sed 's/.*://')"
  # Only pin a real tag. Pinning `latest` would reintroduce the same problem,
  # and an empty value would expand to an invalid image reference.
  if [ -n "$OTHER_TAG" ] && [ "$OTHER_TAG" != latest ]; then
    echo "Pinning $OTHER_SERVICE to its running image tag: $OTHER_TAG"
    export "$OTHER_TAG_VAR=$OTHER_TAG"
  fi
fi

# Full `up -d` so committed changes to db/labels are applied too; Compose only
# recreates a service whose config or image digest actually changed.
dc up -d

CONTAINER="$(dc ps -q "$SERVICE")"
if [ -z "$CONTAINER" ]; then
  echo "ERROR: could not resolve the '$SERVICE' container for project '$PROJECT'" >&2
  dc ps >&2
  exit 1
fi

echo "Waiting for $PROJECT/$SERVICE ($CONTAINER) to become healthy..."
for i in $(seq 1 90); do
  status="$(docker inspect -f '{{if .State.Health}}{{.State.Health.Status}}{{else}}nohealthcheck{{end}}' "$CONTAINER" 2>/dev/null || echo missing)"
  case "$status" in
    healthy|nohealthcheck)
      echo "$PROJECT/$SERVICE up ($status) after ${i}s"
      docker image prune -f >/dev/null 2>&1 || true
      exit 0
      ;;
    unhealthy)
      echo "ERROR: $PROJECT/$SERVICE reported unhealthy" >&2
      docker logs "$CONTAINER" --tail 80 >&2
      exit 1
      ;;
  esac
  sleep 1
done

echo "ERROR: $PROJECT/$SERVICE did not become healthy within 90s (last status: ${status:-unknown})" >&2
docker logs "$CONTAINER" --tail 80 >&2
exit 1
