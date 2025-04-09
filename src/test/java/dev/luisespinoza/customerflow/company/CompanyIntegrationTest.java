package dev.luisespinoza.customerflow.company;

import dev.luisespinoza.customerflow.auth.AuthenticationRequest;
import dev.luisespinoza.customerflow.auth.AuthenticationResponse;
import dev.luisespinoza.customerflow.auth.AuthenticationService;
import dev.luisespinoza.customerflow.exception.ExceptionResponse;
import dev.luisespinoza.customerflow.user.UserRepository;
import dev.luisespinoza.customerflow.user.UserRequest;
import dev.luisespinoza.customerflow.user.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
//@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class CompanyIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    UserService userService;
    @Autowired
    UserRepository userRepository;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() throws Exception {
        companyRepository.deleteAll();
        userRepository.deleteAll();

        UserRequest adminUser = new UserRequest(
                "AdminName",
                "AdminLastname",
                "admin@admin.com",
                "password",
                List.of("ADMIN")
        );
        userService.create(adminUser);

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email(adminUser.getEmail())
                .password(adminUser.getPassword()).build();
        AuthenticationResponse authResponse = authenticationService.authenticate(authRequest);
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authResponse.getToken());
    }

    @Test
    public void givenCompanyRequest_whenCreateCompany_thenCompanyResponseIsReturned() throws Exception {
        CompanyRequest companyRequest = new CompanyRequest();
        companyRequest.setName("The Company");

        HttpEntity<CompanyRequest> requestEntity = new HttpEntity<>(companyRequest, headers);

        ResponseEntity<CompanyResponse> response = testRestTemplate.exchange(
                "/api/v1/company",
                HttpMethod.POST,
                requestEntity,
                CompanyResponse.class );

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("The Company", response.getBody().getName());
    }

    @Test
    public void givenMissingNameCompanyRequest_whenCreateCompany_thenBadRequestExceptionIsReturned() throws Exception {
        CompanyRequest companyRequest = new CompanyRequest();
        HttpEntity<CompanyRequest> requestEntity = new HttpEntity<>(companyRequest, headers);

        ResponseEntity<ExceptionResponse> response = testRestTemplate.exchange(
                "/api/v1/company",
                HttpMethod.POST,
                requestEntity,
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
