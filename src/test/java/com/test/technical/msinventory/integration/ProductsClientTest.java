package com.test.technical.msinventory.integration;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.test.technical.msinventory.api.dto.ProductJsonApiResponse;
import com.test.technical.msinventory.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;
import java.util.UUID;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static org.junit.jupiter.api.Assertions.*;

class ProductsClientTest {

    private WireMockServer wm;
    private ProductsClient client;

    @BeforeEach
    void setUp() {
        wm = new WireMockServer(0);
        wm.start();
        configureFor("localhost", wm.port());

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(500))
                .build();

        JdkClientHttpRequestFactory rf = new JdkClientHttpRequestFactory(httpClient);
        rf.setReadTimeout(Duration.ofMillis(500));

        RestClient restClient = RestClient.builder()
                .baseUrl("http://localhost:" + wm.port())
                .requestFactory(rf)
                .build();

        client = new ProductsClient(restClient, "X-API-KEY", "product-secret-key", 1);
    }

    @AfterEach
    void tearDown() {
        if (wm != null) wm.stop();
    }

    @Test
    void getProduct_ok_shouldParseJsonApi() {
        UUID id = UUID.randomUUID();

        wm.stubFor(get(urlEqualTo("/products/" + id))
                .withHeader("X-API-KEY", equalTo("product-secret-key"))
                .willReturn(aResponse()
                        .withStatus(200)
                        .withHeader("Content-Type", "application/json")
                        .withBody("""
                          {
                            "data": {
                              "type": "products",
                              "id": "%s",
                              "attributes": { "name": "Mouse", "price": 120000.00 }
                            }
                          }
                        """.formatted(id))));

        ProductJsonApiResponse res = client.getProduct(id);

        assertNotNull(res);
        assertNotNull(res.getData());
        assertEquals("products", res.getData().getType());
        assertEquals(id.toString(), res.getData().getId());
        assertEquals("Mouse", res.getData().getAttributes().getName());
    }

    @Test
    void getProduct_404_shouldThrowNotFound() {
        UUID id = UUID.randomUUID();

        wm.stubFor(get(urlEqualTo("/products/" + id))
                .willReturn(aResponse().withStatus(404)));

        assertThrows(NotFoundException.class, () -> client.getProduct(id));
    }

}