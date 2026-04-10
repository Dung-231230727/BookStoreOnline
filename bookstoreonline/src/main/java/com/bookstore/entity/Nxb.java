package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "nxb")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Nxb {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ma_nxb")
    private Integer maNxb;

    @Column(name = "ten_nxb", nullable = false, length = 150)
    private String tenNxb;
}
