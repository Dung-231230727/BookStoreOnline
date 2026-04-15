package com.bookstore.service;

import com.bookstore.dto.*;
import com.bookstore.entity.*;
import com.bookstore.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@SuppressWarnings("null")
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

    public InventoryService(InventoryRepository inventoryRepository,
                         PurchaseOrderRepository purchaseOrderRepository,
                         PurchaseOrderDetailRepository purchaseOrderDetailRepository,
                         SupplierRepository supplierRepository,
                         StaffRepository staffRepository,
                         BookRepository bookRepository,
                         ExportOrderRepository exportOrderRepository,
                         OrderRepository orderRepository,
                         OrderDetailRepository orderDetailRepository,
                         ExportOrderDetailRepository exportOrderDetailRepository) {
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
    }

    @Transactional(readOnly = true)
    public InventoryDetailDTO scanBarcode(String isbn) {
        Inventory inventory = inventoryRepository.findByBook_Isbn(isbn)
                .orElseThrow(() -> new RuntimeException("Item not found in inventory with ISBN: " + isbn));

        if (!(inventory.getBook() instanceof PhysicalBook)) {
            throw new RuntimeException("Warning: ISBN " + isbn + " belongs to an E-Book. E-books do not exist in physical inventory!");
        }

        PhysicalBook physicalBook = (PhysicalBook) inventory.getBook();

        return new InventoryDetailDTO(
                physicalBook.getIsbn(),
                physicalBook.getTitle(),
                inventory.getStockQuantity(),
                inventory.getShelfLocation()
        );
    }

    @Transactional(readOnly = true)
    public List<LowStockAlertDTO> getLowStockAlerts() {
        List<Inventory> lowStockItems = inventoryRepository.findLowStockItems();

        return lowStockItems.stream()
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

        if (request.getSupplierId() == null) throw new IllegalArgumentException("Supplier ID cannot be empty");
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found with ID: " + request.getSupplierId()));

        if (request.getStaffId() == null) throw new IllegalArgumentException("Staff ID cannot be empty");
        Staff staff = staffRepository.findById(request.getStaffId())
                .orElseThrow(() -> new RuntimeException("Staff not found with ID: " + request.getStaffId()));

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
                    .orElseThrow(() -> new RuntimeException("Book not found with ISBN: " + item.getIsbn()));

            if (!(book instanceof PhysicalBook)) {
                throw new RuntimeException("Logic Error: Book " + item.getIsbn() + " (" + book.getTitle() + ") is an E-Book, cannot be imported into physical inventory!");
            }

            PurchaseOrderDetailId detailId = new PurchaseOrderDetailId(purchaseOrderId, item.getIsbn());

            PurchaseOrderDetail detail = new PurchaseOrderDetail();
            detail.setId(detailId);
            detail.setPurchaseOrder(purchaseOrder);
            detail.setBook(book);
            detail.setQuantity(item.getQuantity());
            detail.setUnitPrice(item.getUnitPrice());

            purchaseOrderDetailRepository.save(detail);

            // Note: The trg_update_inventory_on_import trigger in DB handles the stock quantity update
        }

        return new PurchaseOrderResponseDTO(purchaseOrderId, totalAmount, "Stock imported successfully! Inventory updated.");
    }

    @Transactional
    public String exportStock(ExportOrderRequestDTO request) {
        String orderId = request.getOrderId();
        if (orderId == null) throw new IllegalArgumentException("Order ID cannot be empty");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found: " + orderId));

        if (exportOrderRepository.existsByOrder(order)) {
            throw new RuntimeException("This order has already been exported!");
        }

        List<OrderDetail> orderDetails = orderDetailRepository.findByOrder(order);
        if (orderDetails.isEmpty()) {
            throw new RuntimeException("Error: Order has no products!");
        }

        ExportOrder exportOrder = new ExportOrder();
        String exportOrderId = "EX-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        exportOrder.setExportOrderId(exportOrderId);
        exportOrder.setOrder(order);
        exportOrder.setExportDate(LocalDateTime.now());
        exportOrderRepository.save(exportOrder);

        for (OrderDetail detail : orderDetails) {
            if (detail.getBook() instanceof PhysicalBook) {
                ExportOrderDetailId detailId = new ExportOrderDetailId(exportOrderId, detail.getBook().getIsbn());
                ExportOrderDetail exportDetail = new ExportOrderDetail(detailId, detail.getQuantity());
                exportOrderDetailRepository.save(exportDetail);
                // Note: The trg_update_inventory_on_export trigger in DB handles the stock quantity update
            }
        }

        order.setStatusCode("AWAITING_SHIPMENT");
        orderRepository.save(order);

        return "Stock exported successfully for order " + orderId + ". Export ID: " + exportOrderId;
    }
}