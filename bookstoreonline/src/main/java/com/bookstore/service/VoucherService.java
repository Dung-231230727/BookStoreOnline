package com.bookstore.service;

import com.bookstore.dto.VoucherDTO;
import java.util.List;

public interface VoucherService {
    org.springframework.data.domain.Page<VoucherDTO> getAllVouchers(org.springframework.data.domain.Pageable pageable);
    VoucherDTO getVoucherByCode(String code);
    VoucherDTO saveVoucher(VoucherDTO dto);
    void deleteVoucher(String code);
}
