package com.example.flowerstore.services;

import com.example.flowerstore.dto.response.TransactionResponse;
import com.example.flowerstore.dto.response.SepayApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SepayService {
    private final RestTemplate restTemplate;
    
    @Value("${sepay.api.key}")
    private String apiToken;

    public List<TransactionResponse> getTransaction(String transactionContent) {
        String url = "https://my.sepay.vn/userapi/transactions/list?account_number=109883505777&limit=10";
        
        try {
            log.info("Fetching transactions for content: {}", transactionContent);
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiToken);
            
            HttpEntity<?> entity = new HttpEntity<>(headers);
            
            ResponseEntity<SepayApiResponse> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                SepayApiResponse.class
            );
            
            if (response.getBody() != null && 
                response.getBody().getStatus() == 200 && 
                response.getBody().getMessages().getSuccess()) {
                log.info("Received {} transactions", response.getBody().getTransactions().size());
                return response.getBody().getTransactions();
            }
            return null;
        } catch (Exception e) {
            log.error("Error fetching transactions: {}", e.getMessage(), e);
            throw e;
        }
    }
}

