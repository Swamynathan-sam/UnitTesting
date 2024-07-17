package com.sn.springCrudTesting.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import com.sn.springCrudTesting.entity.Customer;

//@SpringBootTest
/**
 * 
 */
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = Replace.NONE)
class CustomerRepositoryTest {
	
	@Container
	@ServiceConnection
	static MySQLContainer<?> mySQLContainer= new MySQLContainer<>(DockerImageName.parse("mysql:latest"));
	
	@Autowired
	CustomerRepository underTest;
	
	@BeforeEach
	void setUp() {
		String email = "swamynathan@gmail.com";
		Customer customer = Customer.create("Swamynathan", email, "India");
		underTest.save(customer);
	}
	
	@AfterEach
	void tearDown() {
		underTest.deleteAll();
	}
	
	@Test
	void canEstablishConnection() {
		assertThat(mySQLContainer.isCreated()).isTrue();
		assertThat(mySQLContainer.isRunning()).isTrue();
	}

	@Test
	void shouldReturnCustomerWhenFindByEmail() {
		//given
		/*
		 * String email = "swamynathan@gmail.com"; Customer customer =
		 * Customer.create("Swamynathan", email, "India"); underTest.save(customer);
		 */
		//When
		Optional<Customer> customerByEmail = underTest.findByEmail("Swamynathan@gmail.com");
		//Then
		assertThat(customerByEmail).isPresent();
	}
	
	@Test
	void shouldNotReturnCustomerwhenFindByEmailNotPresent() {
		//given
		/*
		 * String email = "Yogi@gmail.com"; Customer customer = Customer.create("Yogi",
		 * email, "USA"); underTest.save(customer);
		 */
		//when
		Optional<Customer> customerByEmail = underTest.findByEmail("Yogi@gmail.com");
		//then
		assertThat(customerByEmail).isNotPresent();
	}

}
