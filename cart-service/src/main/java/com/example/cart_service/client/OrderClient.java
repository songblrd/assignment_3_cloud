package com.example.cart_service.client;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import com.example.cart_service.dto.OrderLineRequest;
import com.example.cart_service.dto.OrderResponse;
import com.example.cart_service.exception.BadRequestException;
import com.example.cart_service.exception.OutOfStockException;

// the code cart-service uses to call order-service over HTTP
@Component
public class OrderClient {

    private final RestClient restClient;

    // base URL comes from config (services.order.url), overridden by an env variable in docker-compose
    public OrderClient(@Value("${services.order.url}") String baseUrl) {
        this.restClient = RestClient.builder().baseUrl(baseUrl).build();
    }

    // sends every cart line to POST /api/orders and returns the orders that were created
    public List<OrderResponse> createOrders(List<OrderLineRequest> lines) {
        try {
            return restClient.post()
                .uri("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .body(lines)
                .retrieve()
                .body(new ParameterizedTypeReference<List<OrderResponse>>() {});
        }
        catch (HttpClientErrorException.Conflict ex) {
            // inventory-service refused the reservation, so no orders were saved
            throw new OutOfStockException("Not enough stock for one or more items in your cart.");
        }
        catch (HttpClientErrorException.BadRequest ex) {
            throw new BadRequestException("The order was rejected: one or more products could not be ordered.");
        }
    }
}
