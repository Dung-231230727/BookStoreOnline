package com.bookstore.service;

import com.bookstore.dto.VoucherDTO;
import com.bookstore.entity.Voucher;
import com.bookstore.repository.VoucherRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@SuppressWarnings("null")
public class VoucherServiceImpl implements VoucherService {

    private final VoucherRepository voucherRepository;

    public VoucherServiceImpl(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VoucherDTO> getAllVouchers(Pageable pageable) {
        return voucherRepository.findAll(pageable)
                .map(this::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public VoucherDTO getVoucherByCode(String code) {
        return voucherRepository.findById(code)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Voucher code does not exist"));
    }

    @Override
    @Transactional
    public VoucherDTO saveVoucher(VoucherDTO dto) {
        // BUG-12 fix: Proper upsert — find existing or create new
        Voucher entity = voucherRepository.findById(dto.getVoucherCode())
                .orElse(new Voucher());
        entity.setVoucherCode(dto.getVoucherCode());
        entity.setDiscountValue(dto.getDiscountValue());
        entity.setMinCondition(dto.getMinCondition());
        entity.setExpiryDate(dto.getExpiryDate());

        Voucher saved = voucherRepository.save(entity);
        return toDTO(saved);
    }

    @Override
    @Transactional
    public void deleteVoucher(String code) {
        voucherRepository.deleteById(code);
    }

    private VoucherDTO toDTO(Voucher entity) {
        VoucherDTO dto = new VoucherDTO();
        dto.setVoucherCode(entity.getVoucherCode());
        dto.setDiscountValue(entity.getDiscountValue());
        dto.setMinCondition(entity.getMinCondition());
        dto.setExpiryDate(entity.getExpiryDate());
        return dto;
    }
}

