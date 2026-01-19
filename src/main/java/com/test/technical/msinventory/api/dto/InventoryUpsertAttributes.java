package com.test.technical.msinventory.api.dto;

import jakarta.validation.constraints.Min;

public class InventoryUpsertAttributes {

    @Min(value = 0, message = "quantity must be >= 0")
    private int quantity;

    public InventoryUpsertAttributes() {}

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

}
