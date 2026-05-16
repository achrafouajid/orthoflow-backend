package com.orthoflow.billing.application.service;

import com.orthoflow.billing.application.dto.*;
import com.orthoflow.billing.domain.model.*;
import com.orthoflow.billing.domain.repository.InvoiceRepository;
import com.orthoflow.billing.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BillingService {

    private final InvoiceRepository invoiceRepository;
    private final PaymentRepository paymentRepository;
    private final InvoiceNumberGenerator invoiceNumberGenerator;

    @Transactional
    public InvoiceResponse createInvoice(CreateInvoiceRequest request, UUID creatorId) {
        String invoiceNumber = invoiceNumberGenerator.generate(request.getRegionCode());

        Invoice invoice = Invoice.builder()
                .practiceId(request.getPracticeId())
                .patientId(request.getPatientId())
                .treatmentPlanId(request.getTreatmentPlanId())
                .invoiceNumber(invoiceNumber)
                .status(InvoiceStatus.DRAFT)
                .currency(request.getCurrency())
                .regionCode(request.getRegionCode())
                .notes(request.getNotes())
                .createdBy(creatorId)
                .build();

        BigDecimal subtotal = BigDecimal.ZERO;
        for (InvoiceLineRequest lineReq : request.getLines()) {
            BigDecimal lineTotal = lineReq.getUnitPrice()
                    .multiply(lineReq.getQuantity())
                    .multiply(BigDecimal.ONE.subtract(lineReq.getDiscountPct().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP)));
            
            InvoiceLine line = InvoiceLine.builder()
                    .actCode(lineReq.getActCode())
                    .label(lineReq.getLabel())
                    .quantity(lineReq.getQuantity())
                    .unitPrice(lineReq.getUnitPrice())
                    .discountPct(lineReq.getDiscountPct())
                    .lineTotal(lineTotal)
                    .sortOrder(lineReq.getSortOrder())
                    .build();
            
            invoice.addLine(line);
            subtotal = subtotal.add(lineTotal);
        }

        invoice.setSubtotal(subtotal);
        BigDecimal taxRate = getTaxRate(request.getRegionCode());
        BigDecimal taxAmount = subtotal.multiply(taxRate);
        invoice.setTaxAmount(taxAmount);
        invoice.setTotal(subtotal.add(taxAmount));

        Invoice saved = invoiceRepository.save(invoice);
        return mapToResponse(saved);
    }

    @Transactional
    public void recordPayment(UUID invoiceId, RecordPaymentRequest request, UUID recorderId) {
        Invoice invoice = invoiceRepository.findById(invoiceId)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));

        Payment payment = Payment.builder()
                .invoice(invoice)
                .amount(request.getAmount())
                .method(request.getMethod())
                .paymentDate(request.getPaymentDate())
                .reference(request.getReference())
                .notes(request.getNotes())
                .recordedBy(recorderId)
                .build();

        invoice.addPayment(payment);
        
        BigDecimal totalPaid = invoice.getPayments().stream()
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        if (totalPaid.compareTo(invoice.getTotal()) >= 0) {
            invoice.setStatus(InvoiceStatus.PAID);
        } else if (totalPaid.compareTo(BigDecimal.ZERO) > 0) {
            invoice.setStatus(InvoiceStatus.PARTIALLY_PAID);
        }

        invoiceRepository.save(invoice);
    }
    @Transactional(readOnly = true)
    public List<InvoiceResponse> getInvoices() {
        return invoiceRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public InvoiceResponse getInvoice(UUID id) {
        return invoiceRepository.findById(id)
                .map(this::mapToResponse)
                .orElseThrow(() -> new RuntimeException("Invoice not found"));
    }

    @Transactional(readOnly = true)
    public BillingSummaryResponse getBillingSummary() {
        List<Invoice> invoices = invoiceRepository.findAll();
        
        BigDecimal totalInvoiced = invoices.stream()
                .map(Invoice::getTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        BigDecimal totalCollected = invoices.stream()
                .flatMap(inv -> inv.getPayments().stream())
                .map(Payment::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Map<InvoiceStatus, Long> byStatus = invoices.stream()
                .collect(Collectors.groupingBy(Invoice::getStatus, Collectors.counting()));

        return BillingSummaryResponse.builder()
                .periodStart(LocalDate.now().withDayOfMonth(1))
                .periodEnd(LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth()))
                .totalInvoiced(totalInvoiced)
                .totalCollected(totalCollected)
                .outstandingAmount(totalInvoiced.subtract(totalCollected))
                .invoiceCount(invoices.size())
                .byStatus(byStatus)
                .build();
    }

    private BigDecimal getTaxRate(String regionCode) {
        return switch (regionCode.toUpperCase()) {
            case "FR" -> BigDecimal.valueOf(0.20);
            case "MA" -> BigDecimal.ZERO;
            case "US" -> BigDecimal.valueOf(0.08);
            default -> BigDecimal.ZERO;
        };
    }

    private InvoiceResponse mapToResponse(Invoice invoice) {
        return InvoiceResponse.builder()
                .id(invoice.getId())
                .practiceId(invoice.getPracticeId())
                .patientId(invoice.getPatientId())
                .invoiceNumber(invoice.getInvoiceNumber())
                .status(invoice.getStatus())
                .issueDate(invoice.getIssueDate())
                .dueDate(invoice.getDueDate())
                .currency(invoice.getCurrency())
                .subtotal(invoice.getSubtotal())
                .taxAmount(invoice.getTaxAmount())
                .total(invoice.getTotal())
                .regionCode(invoice.getRegionCode())
                .createdAt(invoice.getCreatedAt())
                .lines(invoice.getLines().stream().map(this::mapLineToResponse).collect(Collectors.toList()))
                .build();
    }

    private InvoiceLineResponse mapLineToResponse(InvoiceLine line) {
        return InvoiceLineResponse.builder()
                .id(line.getId())
                .actCode(line.getActCode())
                .label(line.getLabel())
                .quantity(line.getQuantity())
                .unitPrice(line.getUnitPrice())
                .lineTotal(line.getLineTotal())
                .build();
    }
}
