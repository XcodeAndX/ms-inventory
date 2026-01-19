package com.test.technical.msinventory.api.dto;

import jakarta.validation.constraints.Min;

public class PurchaseAttributes {

    @Min(value = 1, message = "amount must be >= 1")
    private int amount;

    public PurchaseAttributes() {}

    public int getAmount() { return amount; }
    public void setAmount(int amount) { this.amount = amount; }

}
