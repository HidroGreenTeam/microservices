package com.hidrogreen.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.Map;
import java.util.HashMap;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiGatewayApplication.class, args);
	}

	@RestController
	@RequestMapping("/fallback")
	public static class FallbackController {

		@GetMapping("/detection-service")
		public ResponseEntity<Map<String, Object>> detectionServiceFallback() {
			Map<String, Object> response = new HashMap<>();
			response.put("error", "Detection Service no disponible temporalmente");
			response.put("message", "El servicio de detección está experimentando problemas. Por favor, inténtelo más tarde.");
			response.put("status", "SERVICE_UNAVAILABLE");
			return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(response);
		}
	}
}
