package com.example.RCCC03.account.model;

import com.example.RCCC03.customer.model.Customer;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class CustomersResponse {
   List<Customer> customers;
}

