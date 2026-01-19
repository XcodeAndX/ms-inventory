package com.test.technical.msinventory.config;


import com.test.technical.msinventory.integration.ProductsClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestClient;

import java.net.http.HttpClient;
import java.time.Duration;

@Configuration
public class ProductsClientConfig {

    @Bean
    public RestClient productsRestClient(
            @Value("${products.base-url}") String baseUrl,
            @Value("${products.timeout-ms}") long timeoutMs
    ) {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofMillis(timeoutMs))
                .build();

        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofMillis(timeoutMs));

        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    public ProductsClient productsClient(
            RestClient productsRestClient,
            @Value("${products.api-key.header}") String apiKeyHeader,
            @Value("${products.api-key.value}") String apiKeyValue,
            @Value("${products.retry.max-attempts}") int maxAttempts
    ) {
        return new ProductsClient(productsRestClient, apiKeyHeader, apiKeyValue, maxAttempts);
    }

}
