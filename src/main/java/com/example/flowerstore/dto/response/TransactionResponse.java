package com.example.flowerstore.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class TransactionResponse {
    private String id;
    
    @JsonProperty("bank_brand_name")
    private String bankBrandName;
    
    @JsonProperty("account_number")
    private String accountNumber;
    
    @JsonProperty("transaction_date")
    private String transactionDate;
    
    @JsonProperty("amount_out")
    private String amountOut;
    
    @JsonProperty("amount_in")
    private String amountIn;
    
    private String accumulated;
    
    @JsonProperty("transaction_content")
    private String transactionContent;
    
    @JsonProperty("reference_number")
    private String referenceNumber;
    
    private String code;
    
    @JsonProperty("sub_account")
    private String subAccount;
    
    @JsonProperty("bank_account_id")
    private String bankAccountId;
} 