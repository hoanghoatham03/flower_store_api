package com.example.flowerstore.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import java.util.List;

@Data
public class SepayApiResponse {
    private Integer status;
    private String error;
    private Messages messages;
    private List<TransactionResponse> transactions;

    @Data
    public static class Messages {
        private Boolean success;
    }
} 