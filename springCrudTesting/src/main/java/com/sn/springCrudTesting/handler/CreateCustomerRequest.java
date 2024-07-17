package com.sn.springCrudTesting.handler;

public record CreateCustomerRequest(String name,
                                    String email,
                                    String address) {
}