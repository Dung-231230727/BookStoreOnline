package com.bookstore.service;

import com.bookstore.dto.SupplierDTO;
import com.bookstore.entity.Supplier;
import com.bookstore.repository.SupplierRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
public class SupplierService {

    private final SupplierRepository supplierRepository;

    public SupplierService(SupplierRepository supplierRepository) {
        this.supplierRepository = supplierRepository;
    }

    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(supplier -> new SupplierDTO(
                        supplier.getSupplierId(),
                        supplier.getSupplierName(),
                        supplier.getContactInfo()
                ))
                .collect(Collectors.toList());
    }

    public SupplierDTO addSupplier(SupplierDTO dto) {
        Supplier supplier = new Supplier();
        supplier.setSupplierName(dto.getSupplierName());
        supplier.setContactInfo(dto.getContactInfo());

        Supplier saved = supplierRepository.save(supplier);
        return new SupplierDTO(saved.getSupplierId(), saved.getSupplierName(), saved.getContactInfo());
    }

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