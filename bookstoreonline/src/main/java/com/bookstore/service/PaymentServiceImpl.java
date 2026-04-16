package com.bookstore.service;

import com.bookstore.entity.Order;
import com.bookstore.entity.Payment;
import com.bookstore.enums.OrderStatus;
import com.bookstore.enums.PaymentMethod;
import com.bookstore.enums.PaymentStatus;
import com.bookstore.repository.OrderRepository;
import com.bookstore.repository.PaymentRepository;
import com.bookstore.security.VNPayUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;
    private final PaymentRepository paymentRepository;

    @Value("${vnpay.tmn-code}")
    private String vnp_TmnCode;

    @Value("${vnpay.hash-secret}")
    private String vnp_HashSecret;

    @Value("${vnpay.pay-url}")
    private String vnp_PayUrl;

    @Value("${vnpay.return-url}")
    private String vnp_ReturnUrl;

    public PaymentServiceImpl(OrderRepository orderRepository, PaymentRepository paymentRepository) {
        this.orderRepository = orderRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public String createVnpayUrl(String orderId, String clientIp) throws Exception {
        Order order = orderRepository.findById(java.util.Objects.requireNonNull(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Order is not in pending payment status");
        }

        long amount = order.getTotalPayment().longValue() * 100;
        String vnp_TxnRef = orderId + "_" + System.currentTimeMillis();
        
        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment information not found for order: " + orderId));
        payment.setTransactionReference(vnp_TxnRef);
        paymentRepository.save(payment);

        Map<String, String> vnp_Params = new TreeMap<>();
        vnp_Params.put("vnp_Version", "2.1.0");
        vnp_Params.put("vnp_Command", "pay");
        vnp_Params.put("vnp_TmnCode", vnp_TmnCode);
        vnp_Params.put("vnp_Amount", String.valueOf(amount));
        vnp_Params.put("vnp_CurrCode", "VND");
        vnp_Params.put("vnp_TxnRef", vnp_TxnRef);
        vnp_Params.put("vnp_OrderInfo", "Thanh toan don hang " + orderId);
        vnp_Params.put("vnp_OrderType", "other");
        vnp_Params.put("vnp_Locale", "vn");
        vnp_Params.put("vnp_ReturnUrl", vnp_ReturnUrl);
        vnp_Params.put("vnp_IpAddr", clientIp);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
        vnp_Params.put("vnp_CreateDate", LocalDateTime.now().format(formatter));
        vnp_Params.put("vnp_ExpireDate", LocalDateTime.now().plusMinutes(15).format(formatter));

        StringBuilder hashData = new StringBuilder();
        StringBuilder query = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = vnp_Params.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                query.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII));
                query.append('=');
                query.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    query.append('&');
                    hashData.append('&');
                }
            }
        }
        String queryUrl = query.toString();
        String vnp_SecureHash = VNPayUtils.hmacSHA512(vnp_HashSecret, hashData.toString());
        queryUrl += "&vnp_SecureHash=" + vnp_SecureHash;
        
        return vnp_PayUrl + "?" + queryUrl;
    }

    @Override
    @Transactional
    public String processVnpayCallback(Map<String, String> params) {
        String vnp_SecureHash = params.get("vnp_SecureHash");
        Map<String, String> signParams = new TreeMap<>(params);
        signParams.remove("vnp_SecureHashType");
        signParams.remove("vnp_SecureHash");

        StringBuilder hashData = new StringBuilder();
        Iterator<Map.Entry<String, String>> itr = signParams.entrySet().iterator();
        while (itr.hasNext()) {
            Map.Entry<String, String> entry = itr.next();
            String fieldName = entry.getKey();
            String fieldValue = entry.getValue();
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                hashData.append(fieldName);
                hashData.append('=');
                hashData.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII));
                if (itr.hasNext()) {
                    hashData.append('&');
                }
            }
        }

        String checkSum = VNPayUtils.hmacSHA512(vnp_HashSecret, hashData.toString());
        if (checkSum.equalsIgnoreCase(vnp_SecureHash)) {
            String txnRef = params.get("vnp_TxnRef");
            String responseCode = params.get("vnp_ResponseCode");

            Payment payment = paymentRepository.findByTransactionReference(txnRef)
                    .orElseThrow(() -> new RuntimeException("Transaction not found: " + txnRef));

            Order order = payment.getOrder();
            if ("00".equals(responseCode)) {
                payment.setStatus(PaymentStatus.SUCCESS);
                payment.setPaymentDate(LocalDateTime.now());
                order.setStatus(OrderStatus.CONFIRMED); 
            } else {
                payment.setStatus(PaymentStatus.FAILED);
                order.setStatus(OrderStatus.CANCELLED); 
            }
            paymentRepository.save(payment);
            orderRepository.save(order);

            return "SUCCESS";
        }
        return "INVALID_CHECKSUM";
    }

    @Override
    @Transactional
    public String createMockMomoUrl(String orderId) {
        Order order = orderRepository.findById(java.util.Objects.requireNonNull(orderId))
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (order.getStatus() != OrderStatus.NEW) {
            throw new RuntimeException("Order is not in pending payment status");
        }

        Payment payment = paymentRepository.findByOrder_OrderId(orderId)
                .orElseThrow(() -> new RuntimeException("Payment information not found: " + orderId));
        payment.setPaymentMethod(PaymentMethod.MOMO);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepository.save(payment);

        return "http://localhost:8080/web/Orders/PaymentResult?orderId=" + orderId + "&status=success&provider=momo";
    }
}
