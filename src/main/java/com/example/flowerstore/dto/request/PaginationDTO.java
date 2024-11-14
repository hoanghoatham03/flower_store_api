package com.example.flowerstore.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PaginationDTO {
    private int pageNo = 1;
    private int pageSize = 2;
    private String sortBy = "id";
}
