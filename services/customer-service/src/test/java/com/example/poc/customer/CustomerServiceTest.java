package com.example.poc.customer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    @Test
    void createsCustomer() {
        CreateCustomerRequest request = new CreateCustomerRequest("Ada", "Lovelace", "ada@example.com");
        Customer savedCustomer = new Customer("Ada", "Lovelace", "ada@example.com");
        when(customerRepository.save(org.mockito.ArgumentMatchers.any(Customer.class)))
                .thenReturn(savedCustomer);

        CustomerResponse response = customerService.create(request);

        assertThat(response.firstName()).isEqualTo("Ada");
        assertThat(response.lastName()).isEqualTo("Lovelace");
        assertThat(response.email()).isEqualTo("ada@example.com");
        verify(customerRepository).save(org.mockito.ArgumentMatchers.any(Customer.class));
    }

    @Test
    void returnsAllCustomers() {
        when(customerRepository.findAll())
                .thenReturn(List.of(new Customer("Grace", "Hopper", "grace@example.com")));

        List<CustomerResponse> customers = customerService.findAll();

        assertThat(customers).singleElement().satisfies(customer -> {
            assertThat(customer.firstName()).isEqualTo("Grace");
            assertThat(customer.lastName()).isEqualTo("Hopper");
        });
    }
}
