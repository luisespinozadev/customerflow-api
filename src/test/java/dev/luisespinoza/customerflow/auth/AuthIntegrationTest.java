package dev.luisespinoza.customerflow.auth;

import dev.luisespinoza.customerflow.user.UserRepository;
import dev.luisespinoza.customerflow.user.UserRequest;
import dev.luisespinoza.customerflow.user.UserService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = RANDOM_PORT)
@AutoConfigureWebMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthIntegrationTest {

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    private AuthenticationService authenticationService;
    @Autowired
    UserRepository userRepository;
    @Autowired
    UserService userService;

    private UserRequest adminUserRequest;

    @BeforeEach
    public void setUp() throws Exception {
        // User
        userRepository.deleteAll();
        adminUserRequest = new UserRequest(
                "AdminName",
                "AdminLastname",
                "admin@admin.com",
                "password",
                List.of("ADMIN")
        );
        userService.create(adminUserRequest);
    }

    @Test
    public void givenAuthenticationRequest_whenAuthenticate_thenAuthenticationResponseIsReturned() throws Exception {

        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email(adminUserRequest.getEmail()).password(adminUserRequest.getPassword()).build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<AuthenticationRequest> requestEntity = new HttpEntity<>(authRequest, headers);

        ResponseEntity<AuthenticationResponse> response = testRestTemplate.exchange(
                "/api/v1/auth/authenticate",
                HttpMethod.POST,
                requestEntity,
                AuthenticationResponse.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
        Assertions.assertNotNull(response.getBody());
        Assertions.assertNotNull(response.getBody().getToken());
    }

    @Test
    public void givenInvalidAuthenticationRequest_whenAuthenticate_thenUnauthorizedStatusIsReturned() throws Exception {
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email("invalid@email.com").password("invalidCredentials").build();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<AuthenticationRequest> requestEntity = new HttpEntity<>(authRequest, headers);

        ResponseEntity<AuthenticationResponse> response = testRestTemplate.exchange(
                "/api/v1/auth/authenticate",
                HttpMethod.POST,
                requestEntity,
                AuthenticationResponse.class
        );

        Assertions.assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }




}
