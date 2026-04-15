package com.bookstore.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class VoucherDTO {
    private String voucherCode;
    private BigDecimal discountValue;
    private BigDecimal minCondition;
    private LocalDateTime expiryDate;

    public VoucherDTO() {}

    public String getVoucherCode() { return voucherCode; }
    public void setVoucherCode(String voucherCode) { this.voucherCode = voucherCode; }
    public BigDecimal getDiscountValue() { return discountValue; }
    public void setDiscountValue(BigDecimal discountValue) { this.discountValue = discountValue; }
    public BigDecimal getMinCondition() { return minCondition; }
    public void setMinCondition(BigDecimal minCondition) { this.minCondition = minCondition; }
    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }
}
