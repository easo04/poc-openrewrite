package com.example.poc.customer;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse create(CreateCustomerRequest request) {
        Customer customer = new Customer(request.firstName(), request.lastName(), request.email());
        return toResponse(customerRepository.save(customer));
    }

    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(CustomerService::toResponse)
                .toList();
    }

    private static CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail());
    }
}
