package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "van_chuyen")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VanChuyen {

    @Id
    @Column(name = "ma_vanchuyen", length = 30)
    private String maVanChuyen;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_donhang", unique = true, nullable = false)
    private DonHang donHang;

    @Column(name = "doi_tac", length = 50)
    private String doiTac = "GHN";

    @Column(name = "tracking_id", length = 50)
    private String trackingId;

    @Column(name = "trang_thai_tracking", length = 100)
    private String trangThaiTracking;

    @Column(name = "thoi_gian_cap_nhat")
    private LocalDateTime thoiGianCapNhat = LocalDateTime.now();
}
