package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "voucher")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Voucher {

    @Id
    @Column(name = "ma_voucher", length = 20)
    private String maVoucher;

    @Column(name = "gia_tri_giam", nullable = false, precision = 10, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "dieu_kien_toi_thieu", precision = 12, scale = 2)
    private BigDecimal dieu_kien_toi_thieu = BigDecimal.ZERO;

    @Column(name = "thoi_han", nullable = false)
    private LocalDateTime thoiHan;
}
