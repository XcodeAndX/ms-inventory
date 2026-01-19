package com.test.technical.msinventory.service;


import com.test.technical.msinventory.exception.BadRequestException;
import com.test.technical.msinventory.exception.ConflictException;
import com.test.technical.msinventory.exception.NotFoundException;
import com.test.technical.msinventory.integration.ProductsClient;
import com.test.technical.msinventory.persistence.entity.InventoryEntity;
import com.test.technical.msinventory.persistence.repository.InventoryRepository;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository repo;
    private final ProductsClient productsClient;

    public InventoryService(InventoryRepository repo, ProductsClient productsClient) {
        this.repo = repo;
        this.productsClient = productsClient;
    }

    /**
     * ✅ Valida que el producto exista en ms-products y luego consulta inventario.
     */
    @Transactional(readOnly = true)
    public InventoryEntity getByProductId(UUID productId) {
        productsClient.getProduct(productId); // si no existe -> NotFoundException

        return repo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for product"));
    }

    /**
     * Upsert (set de cantidad). También valida que el producto exista.
     */
    @Transactional
    public InventoryEntity upsert(UUID productId, int newQuantity) {
        if (newQuantity < 0) throw new BadRequestException("Quantity cannot be negative");

        productsClient.getProduct(productId);

        InventoryEntity inv = repo.findById(productId)
                .orElseGet(() -> new InventoryEntity(productId, 0));

        inv.setQuantity(newQuantity);
        InventoryEntity saved = repo.save(inv);

        log.info("Inventory changed: productId={} quantity={}", saved.getProductId(), saved.getQuantity());
        return saved;
    }

    /**
     * Compra: descuenta stock. También valida que el producto exista.
     */
    @Transactional
    public InventoryEntity purchase(UUID productId, int amount) {
        if (amount <= 0) throw new BadRequestException("Purchase amount must be > 0");

        productsClient.getProduct(productId);

        InventoryEntity inv = repo.findById(productId)
                .orElseThrow(() -> new NotFoundException("Inventory not found for product"));

        if (inv.getQuantity() < amount) {
            throw new ConflictException("Insufficient inventory");
        }

        inv.setQuantity(inv.getQuantity() - amount);
        InventoryEntity saved = repo.save(inv);

        log.info("Inventory changed: productId={} quantity={}", saved.getProductId(), saved.getQuantity());
        return saved;
    }

}
