package com.example.order_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import com.example.order_service.dto.ProductResponse;
import com.example.order_service.exception.ResourceNotFound;

@Component
public class ProductClient {
    private final RestClient restClient;

    public ProductClient(@Value("${services.product.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public ProductResponse getProduct(Long id){
        try {
            return restClient.get()
               .uri("/api/products/{id}", id)
               .retrieve()
               .body(ProductResponse.class);
        }
        catch (HttpClientErrorException.NotFound ex){
            throw new ResourceNotFound("Product " + id + "Was not found");
        }
    }
}