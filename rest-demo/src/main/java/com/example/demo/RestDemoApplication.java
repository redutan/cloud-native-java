package com.example.demo;

import lombok.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.net.URI;

interface CustomerRepository extends JpaRepository<Customer, Long> {
}

@SpringBootApplication
public class RestDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(RestDemoApplication.class, args);
    }
}

@SuppressWarnings("SpringJavaAutowiredFieldsWarningInspection")
@RestController
@RequestMapping("/v1/customers")
class CustomerRestController {
    @Autowired
    CustomerRepository customerRepository;

    @RequestMapping(method = RequestMethod.HEAD)
    ResponseEntity<?> options() {
        return ResponseEntity.ok()
                .allow(HttpMethod.GET,
                        HttpMethod.HEAD,
                        HttpMethod.POST,
                        HttpMethod.PUT,
                        HttpMethod.PATCH,
                        HttpMethod.DELETE,
                        HttpMethod.OPTIONS)
                .build();
    }

    @GetMapping("/{id}")
    ResponseEntity<Customer> get(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PostMapping
    ResponseEntity<Customer> post(@RequestBody Customer c,
                                  UriComponentsBuilder uriComponentsBuilder) {
        Customer save = customerRepository.save(new Customer(c.getFirstName(), c.getLastName()));

        URI uri = uriComponentsBuilder.path("/{id}")
                .buildAndExpand(save.getId())
                .toUri();
        return ResponseEntity.created(uri).build();
    }

    @DeleteMapping("/{id}")
    ResponseEntity<?> delete(@PathVariable Long id) {
        return customerRepository.findById(id)
                .map(c -> {
                    customerRepository.delete(c);
                    return ResponseEntity.noContent().build();
                })
                .orElseThrow(() -> new CustomerNotFoundException(id));

    }

    @RequestMapping(value = "/{id}", method = RequestMethod.HEAD)
    ResponseEntity<?> head(@PathVariable Long id) {
        return this.customerRepository.findById(id)
                .map(exists -> ResponseEntity.noContent().build())
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }

    @PutMapping(value = "/{id}")
    ResponseEntity<Customer> put(@PathVariable Long id, @RequestBody Customer c) {
        return this.customerRepository
                .findById(id)
                .map(existing -> {
                    Customer customer = this.customerRepository.save(new Customer(
                            existing.getId(), c.getFirstName(), c.getLastName()));
                    URI selfLink = URI.create(ServletUriComponentsBuilder.fromCurrentRequest()
                            .toUriString());
                    return ResponseEntity.created(selfLink).body(customer);
                })
                .orElseThrow(() -> new CustomerNotFoundException(id));
    }
}

@Entity
@Getter
@EqualsAndHashCode
@ToString
@NoArgsConstructor(access = AccessLevel.PACKAGE)
@AllArgsConstructor
class Customer {
    @Id
    @GeneratedValue
    private Long id;
    private String firstName;
    private String lastName;

    public Customer(String firstName, String lastName) {
        this.firstName = firstName;
        this.lastName = lastName;
    }
}

class CustomerNotFoundException extends RuntimeException {
    CustomerNotFoundException(Long id) {
        super("Not found customer : " + id);
    }
}
