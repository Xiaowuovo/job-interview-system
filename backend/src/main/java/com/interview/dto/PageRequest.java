package com.interview.dto;

import lombok.Data;
import javax.validation.constraints.Min;

@Data
public class PageRequest {
    @Min(value = 0, message = "页码不能小于0")
    private Integer page = 0;

    @Min(value = 1, message = "每页大小不能小于1")
    private Integer size = 10;

    private String sortBy = "id";
    private String sortDirection = "DESC";

    public org.springframework.data.domain.PageRequest toSpringPageRequest() {
        org.springframework.data.domain.Sort.Direction direction = 
            "ASC".equalsIgnoreCase(sortDirection) ? 
            org.springframework.data.domain.Sort.Direction.ASC : 
            org.springframework.data.domain.Sort.Direction.DESC;
        
        return org.springframework.data.domain.PageRequest.of(
            page, 
            size, 
            org.springframework.data.domain.Sort.by(direction, sortBy)
        );
    }
}
