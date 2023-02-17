package com.example.RCCC03.provider.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AllProvidersResponse {
    public long count;
    public Iterable<Provider> providers;
}
