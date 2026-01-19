package com.test.technical.msinventory.api.controller;


import com.test.technical.msinventory.api.dto.*;
import com.test.technical.msinventory.persistence.entity.InventoryEntity;
import com.test.technical.msinventory.service.InventoryService;
import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;


@RestController
@RequestMapping(value = "/inventories", produces = MediaType.APPLICATION_JSON_VALUE)
public class InventoryController {


    private static final String TYPE_INVENTORIES = "inventories";

    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @GetMapping("/{productId}")
    public JsonApiResponse<JsonApiData<InventoryAttributes>> get(@PathVariable UUID productId) {
        InventoryEntity inv = service.getByProductId(productId);
        return new JsonApiResponse<>(toData(inv));
    }

    @PatchMapping(value = "/{productId}/purchase", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonApiResponse<JsonApiData<InventoryAttributes>> purchase(
            @PathVariable UUID productId,
            @Valid @RequestBody JsonApiRequest<PurchaseAttributes> request
    ) {
        validateType(request);

        int amount = request.getData().getAttributes().getAmount();
        InventoryEntity updated = service.purchase(productId, amount);

        return new JsonApiResponse<>(toData(updated));
    }

    private void validateType(JsonApiRequest<?> request) {
        if (request == null || request.getData() == null) {
            throw new IllegalArgumentException("Request body must contain data");
        }
        if (request.getData().getType() == null || request.getData().getType().isBlank()) {
            throw new IllegalArgumentException("data.type is required");
        }
        if (!TYPE_INVENTORIES.equals(request.getData().getType())) {
            throw new IllegalArgumentException("Invalid data.type. Expected: " + TYPE_INVENTORIES);
        }
        if (request.getData().getAttributes() == null) {
            throw new IllegalArgumentException("data.attributes is required");
        }
    }

    private JsonApiData<InventoryAttributes> toData(InventoryEntity inv) {
        return new JsonApiData<>(
                TYPE_INVENTORIES,
                inv.getProductId().toString(),
                new InventoryAttributes(inv.getQuantity())
        );
    }

    @PutMapping(value = "/{productId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public JsonApiResponse<JsonApiData<InventoryAttributes>> upsert(
            @PathVariable UUID productId,
            @Valid @RequestBody JsonApiRequest<InventoryUpsertAttributes> request
    ) {
        validateType(request);

        int quantity = request.getData().getAttributes().getQuantity();
        InventoryEntity saved = service.upsert(productId, quantity);

        return new JsonApiResponse<>(toData(saved));
    }

}
