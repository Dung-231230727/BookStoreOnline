package com.bookstore.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "sach_vat_ly")
@PrimaryKeyJoinColumn(name = "isbn")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class SachVatLy extends Sach {

    @Column(name = "can_nang", precision = 5, scale = 2)
    private BigDecimal canNang;
}
