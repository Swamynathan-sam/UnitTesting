package com.sn.springCrudTesting.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sn.springCrudTesting.entity.Customer;
import com.sn.springCrudTesting.exceptions.CustomerDetailsNotFoundException;
import com.sn.springCrudTesting.exceptions.CustomerEmailUnavailableException;
import com.sn.springCrudTesting.exceptions.CustomerNotFoundException;
import com.sn.springCrudTesting.handler.CreateCustomerRequest;
import com.sn.springCrudTesting.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

	CustomerService underTest;
	
	@Mock
	CustomerRepository customerRepository;
	
	@Captor
	ArgumentCaptor<Customer> customArgumentCaptor;
//	ArgumentCaptor is a special Mockito object used to capture arguments passed to mocked methods
	
	@BeforeEach
	void setUp() {
		underTest= new CustomerService(customerRepository);
	}
	
	
	@Test
	void shouldGetAllCustomers() {
		//given
		//when
		underTest.getCustomers();
		//then
		verify(customerRepository).findAll();
	}

	@Test
	void shouldReturnIdNotFoundWhenGetCustomer() {
		//given
		Long id=5L;
		//when
		when(customerRepository.findById(anyLong())).thenReturn(Optional.empty());
		//then
		assertThatThrownBy(()->underTest.getCustomerById(id))
		.isInstanceOf(CustomerDetailsNotFoundException.class)
		.hasMessageContaining("Customer with id " + id + " doesn't found");
	}
	
	@Test
	void ShouldGetCustomerById() {
		//given
		Long id=5L;
		String name="Swamynathan";
		String email="Swamynathan@gmail.com";
		String address="USA";
		Customer customer = Customer.create(id, name, email, address);
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		//when
		Customer customerById = underTest.getCustomerById(id);
		//then
		assertThat(customerById.getId()).isEqualTo(id);
		assertThat(customerById.getName()).isEqualTo(name);
		assertThat(customerById.getEmail()).isEqualTo(email);
		assertThat(customerById.getAddress()).isEqualTo(address);
	}

	@Test
	void shouldCreateCustomer() {
		//given
		CreateCustomerRequest  createCustomerRequest= new CreateCustomerRequest("swamynathan", "sam@gmail.com", "Dubai");
		//when
		underTest.createCustomer(createCustomerRequest);
		//then
		//it is used to capture the values which is being stored
		verify(customerRepository).save(customArgumentCaptor.capture());
		//Storing the Customer values
		Customer customerCaptured= customArgumentCaptor.getValue();
		
		assertThat(customerCaptured.getName()).isEqualTo(createCustomerRequest.name());
		assertThat(customerCaptured.getEmail()).isEqualTo(createCustomerRequest.email());
		assertThat(customerCaptured.getAddress()).isEqualTo(createCustomerRequest.address());
		
	}
	
	@Test
	void shouldNotCreateCustomerAndthrowExceptionWhenCustomerFindByEmailIsPresent() {
		//given
		CreateCustomerRequest  createCustomerRequest= 
				new CreateCustomerRequest("swamynathan", "sam@gmail.com", "Dubai");
		//when
		when(customerRepository.findByEmail(anyString())).thenReturn(Optional.of(new Customer()));
		//then
		assertThatThrownBy(()->
		underTest.createCustomer(createCustomerRequest))
		.isInstanceOf(CustomerEmailUnavailableException.class)
		.hasMessageContaining("The email " + createCustomerRequest.email() + " unavailable.");
	}

	@Test
	void shouldThrowNotFoundWhenGivenInvalidIdWhileUpdateCustomer() {
		//given
		Long id=5L;
		String name="Swamynathan";
		String email="Swamynathan@gmail.com";
		String address="USA";
		//when
		when(customerRepository.findById(id)).thenReturn(Optional.empty());
		//then
		assertThatThrownBy(()-> underTest.updateCustomer(id, name, email, address))
		.isInstanceOf(CustomerNotFoundException.class)
		.hasMessageContaining("Customer with id " + id + " doesn't found");
		
		//This is to verify the save operation is not performed
		verify(customerRepository,never()).save(any());
		
	}
	
	@Test
	void shouldUpdateNameWhenGivenvalidId() {
		//given
		Long id=5L;
		Customer customer= Customer.create(id, "Swamynathan", "swamynathan@gmail.com", "US");
		
		String newName="Swamynathan G H";
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		//when
		
		
		underTest.updateCustomer(id, newName, null, null);
		//then
		//we are capturing customer details
		verify(customerRepository).save(customArgumentCaptor.capture());
		
		Customer capturedCustomer = customArgumentCaptor.getValue();
		
		assertThat(capturedCustomer.getName()).isEqualTo(newName);
		assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
		assertThat(capturedCustomer.getAddress()).isEqualTo(customer.getAddress());
		
	}
	
	@Test
	void shouldThrowEmailunavailableWhenGivenEmailIsAlreadyPresentWhileUpdateCustomer() {
		//given
		Long id =5L;
		Customer customer = Customer.create(id, "Swamynathan", "swamynathan@gmail.com", "US");
		String newEmail="Sam@gmail.com";
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		
		when(customerRepository.findByEmail(newEmail)).thenReturn(Optional.of(customer));
		assertThatThrownBy(()->underTest.updateCustomer(id, customer.getName(), newEmail, customer.getAddress()))
		.isInstanceOf(CustomerEmailUnavailableException.class)
		.hasMessageContaining("The email \"" + newEmail + "\" unavailable to update");
		
		verify(customerRepository,never()).save(customer);

	}
	
	@Test
	void shouldUpdateOnlyCustomerEmail() {
		//given
		Long id=5L;
		Customer customer = Customer.create(id, "Swamynathan", "swamynathan@gmail.com", "US");
		
		String newEmail = "Sam@gmail.com";
		when(customerRepository.findById(id))
			.thenReturn(Optional.of(customer));
		//when
		
		underTest.updateCustomer(id, null, newEmail, null);
		//then
		verify(customerRepository).save(customArgumentCaptor.capture());
		
		Customer capturedCustomer = customArgumentCaptor.getValue();
		
		assertThat(capturedCustomer.getName()).isEqualTo(capturedCustomer.getName());
		assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
		assertThat(capturedCustomer.getAddress()).isEqualTo(capturedCustomer.getAddress());
	}
	
	@Test
	void shouldUpdateOnlyCustomerAddress() {
		//give
		Long id=5L;
		Customer customer = Customer.create(id, "Swamynathan", "swamynathan@gmail.com", "US");
		
		String newAddress= "India";
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));
		
		//when
		underTest.updateCustomer(id, null, null, newAddress);
		//then
		verify(customerRepository).save(customArgumentCaptor.capture());
		
		Customer capturedCustomer = customArgumentCaptor.getValue();
		
		assertThat(capturedCustomer.getName()).isEqualTo(customer.getName());
		assertThat(capturedCustomer.getEmail()).isEqualTo(customer.getEmail());
		assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
		
	}

	@Test
	void shouldUpdateAllAttributeWhenUpdateCustomer() {
		//given
		Long id =5L;
		Customer customer = Customer.create(id, "Swamynathan", "swamynathan@gmail.com", "US");

		String newName = "Yogeesh";
		String newEmail = "yogi@gmail.com";
		String newAddress = "Africa";
		when(customerRepository.findById(id)).thenReturn(Optional.of(customer));

		//when
		underTest.updateCustomer(id, newName, newEmail, newAddress);
		
		verify(customerRepository).save(customArgumentCaptor.capture());
		Customer capturedCustomer = customArgumentCaptor.getValue();
		//then
		
		assertThat(capturedCustomer.getName()).isEqualTo(newName);
		assertThat(capturedCustomer.getEmail()).isEqualTo(newEmail);
		assertThat(capturedCustomer.getAddress()).isEqualTo(newAddress);
	}
	
	@Test
	void shouldThrowNotFoudWhenGivenIdDoesNotExistsWhileDeleteCustomer() {
		//given
		Long id=5L;
		//To verify whether id is present or not we use repo.existsById(id)
		when(customerRepository.existsById(id)).thenReturn(false);
		//when
		//then
		assertThatThrownBy(()->underTest.deleteCustomer(id))
		.isInstanceOf(CustomerNotFoundException.class)
		.hasMessageContaining("Customer with id " + id + " doesn't exist.");
		
	}
	
	@Test
	void shouldDeleteCustomer() {
		//given
		Long id =5L;
		when(customerRepository.existsById(id)).thenReturn(true);
		//when
		underTest.deleteCustomer(id);
		//then
		verify(customerRepository).deleteById(id);
	}

}
