package com.example.order_service.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import java.util.List;
import com.example.order_service.dto.OrderRequest;
import com.example.order_service.exception.OutOfStockException;

@Component 
public class InventoryClient {
    
    private final RestClient restClient;

    public InventoryClient( @Value("${services.inventory.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    public void reserve(List<OrderRequest> lines){
        try {
            restClient.post()
               .uri("api/inventory/reserve")
               .contentType(MediaType.APPLICATION_JSON)
               .body(lines)
               .retrieve()
               .toBodilessEntity();
        }
        catch (HttpClientErrorException ex) {
            throw new OutOfStockException("One of your items is out of stock");
            // inventory-service returns 409 without identifying which line is short,
            // so a generic message is shown to the shopper.
    }
  }
}
