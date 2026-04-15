package com.bookstore.service;

import com.bookstore.dto.PublisherDTO;
import com.bookstore.entity.Publisher;
import com.bookstore.repository.PublisherRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PublisherService {

    private final PublisherRepository publisherRepository;

    public PublisherService(PublisherRepository publisherRepository) {
        this.publisherRepository = publisherRepository;
    }

    @Transactional(readOnly = true)
    public List<PublisherDTO> getAllPublishers() {
        return publisherRepository.findAll()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private PublisherDTO convertToDTO(Publisher entity) {
        PublisherDTO dto = new PublisherDTO();
        dto.setPublisherId(entity.getPublisherId());
        dto.setPublisherName(entity.getPublisherName());
        return dto;
    }
}
