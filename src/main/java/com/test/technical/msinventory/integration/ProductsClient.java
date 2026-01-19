package com.test.technical.msinventory.integration;

import com.test.technical.msinventory.api.dto.ProductJsonApiResponse;
import com.test.technical.msinventory.exception.CommunicationException;
import com.test.technical.msinventory.exception.NotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.UUID;

public class ProductsClient {


    private final RestClient restClient;
    private final String apiKeyHeader;
    private final String apiKeyValue;
    private final int maxAttempts;

    public ProductsClient(
            RestClient productsRestClient,
            @Value("${products.api-key.header}") String apiKeyHeader,
            @Value("${products.api-key.value}") String apiKeyValue,
            @Value("${products.retry.max-attempts:2}") int maxAttempts
    ) {
        this.restClient = productsRestClient;
        this.apiKeyHeader = apiKeyHeader;
        this.apiKeyValue = apiKeyValue;
        this.maxAttempts = Math.max(1, maxAttempts);
    }

    public ProductJsonApiResponse getProduct(UUID productId) {
        int attempt = 0;
        while (true) {
            attempt++;
            try {
                return restClient.get()
                        .uri("/products/{id}", productId)
                        .header(apiKeyHeader, apiKeyValue)
                        .retrieve()
                        .onStatus(HttpStatusCode::is4xxClientError, (req, res) -> {
                            // si es 404, lo tratamos como producto no encontrado
                            if (res.getStatusCode().value() == 404) {
                                throw new NotFoundException("Product not found");
                            }
                            throw new CommunicationException("Products service returned 4xx: " + res.getStatusCode().value());
                        })
                        .onStatus(HttpStatusCode::is5xxServerError, (req, res) -> {
                            throw new CommunicationException("Products service returned 5xx: " + res.getStatusCode().value());
                        })
                        .body(ProductJsonApiResponse.class);

            } catch (NotFoundException e) {
                throw e;
            } catch (RestClientResponseException e) {
                // errores HTTP que no entraron arriba por alguna razón
                if (e.getStatusCode().value() == 404) throw new NotFoundException("Product not found");
                if (attempt >= maxAttempts) throw new CommunicationException("Products service error: " + e.getMessage(), e);
            } catch (RestClientException e) {
                // timeout / conexión caída
                if (attempt >= maxAttempts) throw new CommunicationException("Products service unreachable", e);
            }
        }
    }
}
