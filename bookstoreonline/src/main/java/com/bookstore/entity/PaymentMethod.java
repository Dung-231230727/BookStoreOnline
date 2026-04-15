package com.bookstore.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "payment_methods")
public class PaymentMethod {
    @Id
    @Column(name = "method_code", length = 50)
    private String methodCode;

    public PaymentMethod() {}

    public PaymentMethod(String methodCode) {
        this.methodCode = methodCode;
    }

    public String getMethodCode() {
        return methodCode;
    }

    public void setMethodCode(String methodCode) {
        this.methodCode = methodCode;
    }

    @Override
    public String toString() {
        return "PaymentMethod{" +
                "methodCode='" + methodCode + '\'' +
                '}';
    }
}
