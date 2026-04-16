package com.bookstore.service;

import java.util.Map;

public interface PaymentService {
    String createVnpayUrl(String orderId, String clientIp) throws Exception;
    String processVnpayCallback(Map<String, String> params);
    String createMockMomoUrl(String orderId);
}
