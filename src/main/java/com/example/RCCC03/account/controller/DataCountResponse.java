package com.example.RCCC03.account.controller;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DataCountResponse<T> {
    public long count;
    public Iterable<T> data;
}
