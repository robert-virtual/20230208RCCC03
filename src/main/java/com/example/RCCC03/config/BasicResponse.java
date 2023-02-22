package com.example.RCCC03.config;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class BasicResponse {
    private String message;
    private String error;

}
