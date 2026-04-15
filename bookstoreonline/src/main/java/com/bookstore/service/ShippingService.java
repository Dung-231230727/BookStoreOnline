package com.bookstore.service;

import com.bookstore.dto.TrackingResponseDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ShippingService {

    public TrackingResponseDTO trackOrder(String orderId) {
        // Mock logic: Returns a simulated tracking history
        
        List<TrackingResponseDTO.TrackingDetail> history = List.of(
                new TrackingResponseDTO.TrackingDetail("10:30 14-04-2026", "In Transit", "Cau Giay Post Office, Hanoi"),
                new TrackingResponseDTO.TrackingDetail("08:15 14-04-2026", "Arrived at destination post office", "Cau Giay Post Office, Hanoi"),
                new TrackingResponseDTO.TrackingDetail("22:00 13-04-2026", "In Transit", "SOC Classifying Warehouse Hanoi"),
                new TrackingResponseDTO.TrackingDetail("18:45 13-04-2026", "Successfully picked up", "BookStore Online Central Warehouse")
        );

        // Fake tracking ID based on order ID
        String fakeTrackingNumber = "GHN-" + orderId.replace("ORD-", "") + "-9981";

        return new TrackingResponseDTO(
                orderId,
                "Giao Hang Nhanh (GHN)",
                fakeTrackingNumber,
                "IN_TRANSIT",
                history
        );
    }

    public void updateShippingStatus(String trackingId, String status, String notes) {
        // In a real scenario, this would integrate with shipping provider APIs
        
        System.out.println("Updating shipping " + trackingId + " to status: " + status);
        if (notes != null && !notes.isEmpty()) {
            System.out.println("Notes: " + notes);
        }
    }
}