package dev.luisespinoza.customerflow.user;

import dev.luisespinoza.customerflow.auth.AuthenticationRequest;
import dev.luisespinoza.customerflow.auth.AuthenticationResponse;
import dev.luisespinoza.customerflow.auth.AuthenticationService;
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
public class UserIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    private AuthenticationService authenticationService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    private HttpHeaders headers;

    @BeforeEach
    public void setUp() throws Exception {
        // User
        userRepository.deleteAll();
        UserRequest adminUser = new UserRequest(
                "AdminName",
                "AdminLastname",
                "admin@admin.com",
                "password",
                List.of("ADMIN")
        );
        userService.create(adminUser);

        // Auth
        AuthenticationRequest authRequest = AuthenticationRequest.builder()
                .email(adminUser.getEmail())
                .password(adminUser.getPassword()).build();
        AuthenticationResponse authResponse = authenticationService.authenticate(authRequest);
        headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + authResponse.getToken());
    }

    @Test
    public void givenGetRequest_whenGetString_thenStringIsReturned() {

        HttpEntity<String> requestEntity = new HttpEntity<>(null,headers);

        ResponseEntity<String> response = testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.GET,
                requestEntity,
                String.class
        );

        Assertions.assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    public void givenUserRequest_whenCreateUser_thenUserResponseIsReturned() throws Exception {
        UserRequest newAdminUserRequest = new UserRequest(
                "FirstnameTest",
                "LastnameTest",
                "test@test.com",
                "test1234",
                List.of("ADMIN")
        );
        HttpEntity<UserRequest> requestEntity = new HttpEntity<>(newAdminUserRequest, headers);

        ResponseEntity<UserResponse> response = testRestTemplate.exchange(
                "/api/v1/users",
                HttpMethod.POST,
                requestEntity,
                UserResponse.class
        );
        System.out.println("Response body: " + response.getBody());
        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
