#!/usr/bin/env bash
# OrthoFlow production deploy. Run from the checkout root on the host
# (/srv/bento/apps/ortho). Keeps this repo and the frontend clone in sync with
# GitHub, then rebuilds and restarts the stack. Requires a populated .env
# (see .env.example).
set -euo pipefail

cd "$(dirname "$0")"

echo "==> Pull backend (this repo)"
git pull --ff-only

echo "==> Sync frontend clone"
if [ -d frontend/.git ]; then
  git -C frontend pull --ff-only
else
  git clone https://github.com/achrafouajid/orthoflow-front.git frontend
fi

echo "==> Build images"
docker compose build --no-cache

echo "==> Start stack"
docker compose up -d

echo "==> Prune dangling images"
docker image prune -f

docker compose ps
