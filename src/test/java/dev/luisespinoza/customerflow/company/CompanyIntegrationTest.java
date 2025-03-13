package dev.luisespinoza.customerflow.company;

import dev.luisespinoza.customerflow.exception.ExceptionResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@SpringBootTest(webEnvironment = RANDOM_PORT)
@ExtendWith(SpringExtension.class)
public class CompanyIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    CompanyRepository repository;

    @BeforeEach
    public void setUp() {
        repository.deleteAll();
    }

    @Test
    public void givenCompanyRequest_whenCreateCompany_thenCompanyResponseIsReturned() throws Exception {
        CompanyRequest request = new CompanyRequest();
        request.setName("The Company");

        ResponseEntity<CompanyResponse> response = testRestTemplate.postForEntity(
                "/api/v1/company",
                request,
                CompanyResponse.class );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Company", response.getBody().getName());
    }

    @Test
    public void givenMissingNameCompanyRequest_whenCreateCompany_thenBadRequestExceptionIsReturned() throws Exception {
        CompanyRequest request = new CompanyRequest();

        ResponseEntity<ExceptionResponse> response = testRestTemplate.postForEntity(
                "/api/v1/company",
                request,
                ExceptionResponse.class
        );

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertNotNull(response.getBody());

        Set<String> validationErrors = response.getBody().getValidationErrors();
        assertNotNull(validationErrors);
        assertFalse(validationErrors.isEmpty());

        assertTrue(validationErrors.contains("The company name cannot be empty"));
    }

}
