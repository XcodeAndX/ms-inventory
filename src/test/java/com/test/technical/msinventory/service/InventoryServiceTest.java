package com.test.technical.msinventory.service;

import com.test.technical.msinventory.exception.ConflictException;
import com.test.technical.msinventory.exception.NotFoundException;
import com.test.technical.msinventory.integration.ProductsClient;
import com.test.technical.msinventory.persistence.entity.InventoryEntity;
import com.test.technical.msinventory.persistence.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    InventoryRepository repo;
    @Mock
    ProductsClient productsClient;

    @InjectMocks
    InventoryService service;

    @Test
    void purchase_ok_shouldDiscount() {
        UUID id = UUID.randomUUID();
        InventoryEntity inv = new InventoryEntity(id, 10);

        when(repo.findById(id)).thenReturn(Optional.of(inv));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        when(productsClient.getProduct(id)).thenReturn(null); // no nos importa el body aquí

        InventoryEntity updated = service.purchase(id, 3);

        assertEquals(7, updated.getQuantity());
        verify(productsClient).getProduct(id);
        verify(repo).save(inv);
    }

    @Test
    void purchase_insufficient_shouldThrowConflict() {
        UUID id = UUID.randomUUID();
        InventoryEntity inv = new InventoryEntity(id, 2);

        when(repo.findById(id)).thenReturn(Optional.of(inv));
        when(productsClient.getProduct(id)).thenReturn(null);

        assertThrows(ConflictException.class, () -> service.purchase(id, 3));
    }

    @Test
    void get_inventoryNotFound_shouldThrowNotFound() {
        UUID id = UUID.randomUUID();
        when(repo.findById(id)).thenReturn(Optional.empty());
        when(productsClient.getProduct(id)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.getByProductId(id));
    }

}