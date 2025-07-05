package com.hidrogreen.payment.gateway.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HidrogreenPaymentGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(HidrogreenPaymentGatewayApplication.class, args);
	}

}
