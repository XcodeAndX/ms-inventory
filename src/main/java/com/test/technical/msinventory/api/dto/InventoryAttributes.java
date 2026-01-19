package com.test.technical.msinventory.api.dto;

public class InventoryAttributes {
    private int quantity;

    public InventoryAttributes() {}

    public InventoryAttributes(int quantity) {
        this.quantity = quantity;
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
}
