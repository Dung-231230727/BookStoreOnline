package com.bookstore.service;

import com.bookstore.dto.*;
import com.bookstore.entity.*;
import com.bookstore.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderDetailRepository purchaseOrderDetailRepository;
    private final SupplierRepository supplierRepository;
    private final StaffRepository staffRepository;
    private final BookRepository bookRepository;
    private final ExportOrderRepository exportOrderRepository;
    private final OrderRepository orderRepository;
    private final OrderDetailRepository orderDetailRepository;
    private final ExportOrderDetailRepository exportOrderDetailRepository;
    private final PhysicalBookRepository physicalBookRepository;
    private final InventoryLogRepository inventoryLogRepository;
    private final AuditLogService auditLogService;

    public InventoryService(InventoryRepository inventoryRepository,
                            PurchaseOrderRepository purchaseOrderRepository,
                            PurchaseOrderDetailRepository purchaseOrderDetailRepository,
                            SupplierRepository supplierRepository,
                            StaffRepository staffRepository,
                            BookRepository bookRepository,
                            ExportOrderRepository exportOrderRepository,
                            OrderRepository orderRepository,
                            OrderDetailRepository orderDetailRepository,
                            ExportOrderDetailRepository exportOrderDetailRepository,
                            PhysicalBookRepository physicalBookRepository,
                            InventoryLogRepository inventoryLogRepository,
                            AuditLogService auditLogService) {
        this.inventoryRepository = inventoryRepository;
        this.purchaseOrderRepository = purchaseOrderRepository;
        this.purchaseOrderDetailRepository = purchaseOrderDetailRepository;
        this.supplierRepository = supplierRepository;
        this.staffRepository = staffRepository;
        this.bookRepository = bookRepository;
        this.exportOrderRepository = exportOrderRepository;
        this.orderRepository = orderRepository;
        this.orderDetailRepository = orderDetailRepository;
        this.exportOrderDetailRepository = exportOrderDetailRepository;
        this.physicalBookRepository = physicalBookRepository;
        this.inventoryLogRepository = inventoryLogRepository;
        this.auditLogService = auditLogService;
    }

    @Transactional(readOnly = true)
    public InventoryDetailDTO scanBarcode(String isbn) {
        Inventory inventory = inventoryRepository.findByBook_Isbn(isbn)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy sản phẩm trong kho với mã ISBN: " + isbn));

        // Refactored to Composition: Check if record exists in PhysicalBook table
        if (!physicalBookRepository.existsById(java.util.Objects.requireNonNull(isbn))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Warning: ISBN " + isbn + " belongs to an E-Book. E-books do not exist in physical inventory!");
        }

        return new InventoryDetailDTO(
                isbn,
                inventory.getBook().getTitle(),
                inventory.getStockQuantity(),
                inventory.getShelfLocation()
        );
    }

    @Transactional(readOnly = true)
    public List<LowStockAlertDTO> getLowStockAlerts() {
        List<Inventory> all = inventoryRepository.findAll();

        return all.stream()
                .filter(inv -> inv.getStockQuantity() <= inv.getAlertThreshold())
                .map(inventory -> new LowStockAlertDTO(
                        inventory.getBook().getIsbn(),
                        inventory.getBook().getTitle(),
                        inventory.getStockQuantity(),
                        inventory.getAlertThreshold()
                ))
                .collect(Collectors.toList());
    }

    @Transactional
    public PurchaseOrderResponseDTO importStock(PurchaseOrderRequestDTO request) {
        if (request.getSupplierId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Supplier ID cannot be empty");
        }
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Supplier not found with ID: " + request.getSupplierId()));

        if (request.getStaffId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Staff ID cannot be empty");
        }
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Staff not found with ID: " + request.getStaffId()));

        BigDecimal totalAmount = request.getDetails().stream()
                .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String purchaseOrderId = "PO-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PurchaseOrder purchaseOrder = new PurchaseOrder();
        purchaseOrder.setPurchaseOrderId(purchaseOrderId);
        purchaseOrder.setSupplier(supplier);
        purchaseOrder.setStaff(staff);
        purchaseOrder.setOrderDate(LocalDateTime.now());
        purchaseOrder.setTotalAmount(totalAmount);

        purchaseOrderRepository.save(purchaseOrder);

        for (PurchaseOrderDetailRequest item : request.getDetails()) {
            if (item.getIsbn() == null) continue;
            
            Book book = bookRepository.findById(item.getIsbn())
                    .orElseGet(() -> {
                        // 1. Tạo bản ghi Book mới
                        Book newBook = new Book();
                        newBook.setIsbn(item.getIsbn());
                        newBook.setTitle(item.getTitle() != null ? item.getTitle() : "Sách mới");
                        // Giá bán mặc định = giá nhập * 1.2
                        newBook.setPrice(item.getUnitPrice().multiply(new BigDecimal("1.2")));
                        Book savedBook = bookRepository.save(newBook);

                        // 2. Tạo bản ghi PhysicalBook tương ứng (composition)
                        PhysicalBook pb = new PhysicalBook();
                        pb.setBook(savedBook);
                        pb.setIsbn(savedBook.getIsbn());
                        physicalBookRepository.save(pb);

                        return savedBook;
                    });

            // Chặn đứng nhập kho nếu là sách điện tử (Phải có bản ghi trong physical_books)
            if (!physicalBookRepository.existsById(book.getIsbn())) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Logic Error: Sách " + book.getTitle() + " là E-Book, không thể nhập kho vật lý!");
            }

            // 2. CẬP NHẬT GIÁ THAM CHIẾU (Ref Price)
            if (book.getPrice() == null || book.getPrice().compareTo(BigDecimal.ZERO) == 0) {
                book.setPrice(item.getUnitPrice().multiply(new BigDecimal("1.2")));
                bookRepository.save(book);
            }

            // 3. CẬP NHẬT TỒN KHO THỰC TẾ (Inventory)
            Inventory inv = inventoryRepository.findByBook_Isbn(item.getIsbn())
                    .orElseGet(() -> {
                        Inventory newInv = new Inventory();
                        newInv.setBook(book);
                        newInv.setStockQuantity(0);
                        newInv.setAlertThreshold(5);
                        return newInv;
                    });

            inv.setStockQuantity((inv.getStockQuantity() == null ? 0 : inv.getStockQuantity()) + item.getQuantity());
            inv.setShelfLocation(item.getShelfLocation());
            inventoryRepository.save(inv);

            // 4. LƯU CHI TIẾT PHIẾU NHẬP
            PurchaseOrderDetailId detailId = new PurchaseOrderDetailId(purchaseOrderId, item.getIsbn());

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setId(detailId);
            detail.setPurchaseOrder(purchaseOrder);
            detail.setBook(book);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());

            purchaseOrderDetailRepository.save(detail);
            
            logInventoryChange(book, staff, "IMPORT", item.getQuantity(), "Purchased via " + purchaseOrderId);
        }

        return new PurchaseOrderResponseDTO(purchaseOrderId, totalAmount, "Stock imported successfully!");
    }

    @Transactional
    public String exportStock(ExportOrderRequestDTO request) {
        String orderId = request.getOrderId();
        if (orderId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order ID cannot be empty");
        }

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Order not found: " + orderId));

        if (exportOrderRepository.existsByOrder(order)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This order has already been exported!");
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        if (orderDetails.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Error: Order has no products!");
        }

        ExportOrder exportOrder = new ExportOrder();
        String exportOrderId = "EX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        exportOrder.setExportOrderId(exportOrderId);
        exportOrder.setOrder(order);
        exportOrder.setExportDate(LocalDateTime.now());
        exportOrderRepository.save(exportOrder);

        for (OrderDetail detail : orderDetails) {
            String isbn = detail.getBook().getIsbn();
            if (physicalBookRepository.existsById(java.util.Objects.requireNonNull(isbn))) {
                ExportOrderDetailId detailId = new ExportOrderDetailId(exportOrderId, isbn);
                ExportOrderDetail exportDetail = new ExportOrderDetail(detailId, detail.getQuantity());
                exportOrderDetailRepository.save(exportDetail);
                
                logInventoryChange(detail.getBook(), null, "EXPORT", -detail.getQuantity(), "Exported for Order " + orderId);
            }
        }

        order.setStatusCode("AWAITING_SHIPMENT");
        orderRepository.save(order);

        return "Stock exported successfully for order " + orderId + ". Export ID: " + exportOrderId;
    }

    @Transactional
    public void adjustStock(String isbn, Integer newQuantity, String reason, Integer staffId) {
        Inventory inv = inventoryRepository.findByBook_Isbn(isbn)
                .orElseThrow(() -> new RuntimeException("Inventory record not found for ISBN: " + isbn));
        
        Staff staff = staffId != null ? staffRepository.findById(staffId).orElse(null) : null;
        
        int oldQty = inv.getStockQuantity();
        int diff = newQuantity - oldQty;
        
        inv.setStockQuantity(newQuantity);
        inventoryRepository.save(inv);
        
        logInventoryChange(inv.getBook(), staff, "ADJUST", diff, reason);
        
        if (staff != null && staff.getAccount() != null) {
            String details = String.format("Stock Adjustment for %s [%s]: %d -> %d. Reason: %s", 
                    inv.getBook().getTitle(), isbn, oldQty, newQuantity, reason);
            auditLogService.log(staff.getAccount(), "STOCK_ADJUSTMENT", details);
        }
    }

    private void logInventoryChange(Book book, Staff staff, String type, int change, String notes) {
        Inventory inv = inventoryRepository.findByBook_Isbn(book.getIsbn()).orElse(null);
        int after = (inv != null) ? inv.getStockQuantity() : 0;
        
        InventoryLog log = new InventoryLog();
        log.setBook(book);
        log.setStaff(staff);
        log.setChangeType(type);
        log.setQuantityChanged(change);
        log.setQuantityAfter(after);
        log.setTimestamp(LocalDateTime.now());
        log.setNotes(notes);
        inventoryLogRepository.save(log);
    }

    public List<InventoryDetailDTO> getAllInventory() {
        return inventoryRepository.findAllInventoryDetails();
    }
}