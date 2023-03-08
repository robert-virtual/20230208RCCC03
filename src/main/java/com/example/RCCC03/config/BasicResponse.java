package com.example.RCCC03.config;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class BasicResponse<T> {
    private T data;
    private int data_count;
    private String data_type;
    private String error;

}
