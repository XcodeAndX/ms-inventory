package com.test.technical.msinventory.persistence.entity;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "inventory")
public class InventoryEntity {
    @Id
    @Column(name = "product_id", nullable = false, updatable = false)
    private UUID productId;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected InventoryEntity() {

    }

    public InventoryEntity(UUID productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
        this.updatedAt = OffsetDateTime.now();
    }

    public UUID getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
        this.updatedAt = OffsetDateTime.now();
    }
}
