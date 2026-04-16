package com.bookstore.service;

import com.bookstore.dto.SupplierDTO;
import com.bookstore.entity.Supplier;
import com.bookstore.repository.SupplierRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    @Transactional(readOnly = true)
    public org.springframework.data.domain.Page<SupplierDTO> getAllSuppliers(org.springframework.data.domain.Pageable pageable) {
        return supplierRepository.findAll(pageable)
                .map(supplier -> new SupplierDTO(
                        supplier.getSupplierId(),
                        supplier.getSupplierName(),
                        supplier.getContactInfo()
                ));
    }

    @Transactional
    public SupplierDTO addSupplier(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactInfo(dto.getContactInfo());

        Supplier saved = supplierRepository.save(supplier);
        return new SupplierDTO(saved.getSupplierId(), saved.getSupplierName(), saved.getContactInfo());
    }

    @Transactional
    public SupplierDTO updateSupplier(Integer id, SupplierDTO dto) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found!"));

        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactInfo(dto.getContactInfo());

        Supplier updated = supplierRepository.save(supplier);
        return new SupplierDTO(updated.getSupplierId(), updated.getSupplierName(), updated.getContactInfo());
    }

    public void deleteSupplier(Integer id) {
        if (!supplierRepository.existsById(id)) {
            throw new RuntimeException("Supplier not found!");
        }
        supplierRepository.deleteById(id);
    }
}