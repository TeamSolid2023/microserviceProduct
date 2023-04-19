package com.gftraining.microservice_product.model;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

public class ResponseHandler {
    public static ResponseEntity<Object> generateResponse(String message, HttpStatus status, Long id) {
        Map<String, Object> map = new HashMap<>();
            map.put("message", message);
            map.put("status", status.value());
            map.put("id", id);

            return new ResponseEntity<>(map,status);
    }

    private ResponseHandler() {
    }
}
