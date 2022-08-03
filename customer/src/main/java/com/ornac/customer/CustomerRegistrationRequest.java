package com.ornac.customer;

public record CustomerRegistrationRequest(
        String firstName,
        String lastName,
        String email
) {
}
