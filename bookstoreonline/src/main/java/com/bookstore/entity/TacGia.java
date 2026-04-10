package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "tac_gia")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_tacgia")
    private Integer maTacGia;

    @Column(name = "ten_tacgia", nullable = false, length = 100)
    private String tenTacGia;

    @Column(name = "tieu_su", columnDefinition = "NVARCHAR(MAX)")
    private String tieuSu;
}
