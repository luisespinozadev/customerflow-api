package dev.luisespinoza.customerflow.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luisespinoza.customerflow.security.JwtService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // Disable security filters
public class UserControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtService jwtService; // Needed to load ApplicationContext

    private UserRequest userRequest;
    private UserResponse userResponse;
    private UserRequest adminUserRequest;
    private UserResponse adminUserResponse;
    private List<UserResponse> userResponseList;

    @BeforeEach
    public void setUp() {
        // ADMIN USER
        adminUserRequest = new UserRequest(
                "AdminFirstName",
                "AdminLastName",
                "admin@mail.com",
                "password",
                List.of("ADMIN")
        );
        adminUserResponse = new UserResponse(
                1L,
                adminUserRequest.getFirstname(),
                adminUserRequest.getLastname(),
                adminUserRequest.getEmail(),
                adminUserRequest.getRoles()
        );

        // USER
        userRequest = new UserRequest(
                "UserFirstName",
                "UserLastName",
                "user@mail.com",
                "12345678",
                List.of("USER")
        );
        userResponse = new UserResponse(
                2L,
                userRequest.getFirstname(),
                userRequest.getLastname(),
                userRequest.getEmail(),
                userRequest.getRoles()
        );

        userResponseList = List.of(adminUserResponse, userResponse);

    }

    @Test
    public void givenUserRequest_whenCreateUser_thenUserResponseIsReturned() throws Exception {

        given(userService.create(any(UserRequest.class)))
                .willReturn(userResponse);

        ResultActions response = mockMvc.perform(
                post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
        );

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value(userRequest.getFirstname()))
                .andExpect(jsonPath("$.email").value(userRequest.getEmail()))
                .andExpect(jsonPath("$.roles[0]").value(userRequest.getRoles().get(0)));
    }

    @Test
    public void givenEmptyEmailUserRequest_whenCreateUser_thenBadRequestExceptionIsReturned() throws Exception {
        UserRequest userRequest = new UserRequest(
                "Firstname",
                "Lastname",
                "",
                "changeme",
                List.of("ADMIN"));

        ResultActions response = mockMvc.perform(
                post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
        );

        response.andExpect(status().isBadRequest());
    }

    @Test
    public void givenExistingUsers_whenFindAllUsers_thenAllUsersAreReturned() throws Exception {
        given(userService.findAll()).willReturn(userResponseList);

        ResultActions response = mockMvc.perform(
                get("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(userResponseList.size()))
                .andExpect(jsonPath("$[0].id").value(adminUserResponse.getId()))
                .andExpect(jsonPath("$[0].firstname").value(adminUserResponse.getFirstname()))
                .andExpect(jsonPath("$[0].email").value(adminUserResponse.getEmail()))
                .andExpect(jsonPath("$[0].roles[0]").value(adminUserResponse.getRoles().get(0)))
                .andExpect(jsonPath("$[1].id").value(userResponse.getId()))
                .andExpect(jsonPath("$[1].lastname").value(userResponse.getLastname()))
                .andExpect(jsonPath("$[1].roles[0]").value(userResponse.getRoles().get(0)));
    }

    @Test
    public void givenExistingUser_whenFindById_thenUserResponseIsReturned() throws Exception {
        Long userId = userResponse.getId();
        given(userService.findById(userId)).willReturn(userResponse);

        ResultActions response = mockMvc.perform(
                get("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
        );

        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userResponse.getId()))
                .andExpect(jsonPath("$.firstname").value(userResponse.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(userResponse.getLastname()))
                .andExpect(jsonPath("$.email").value(userResponse.getEmail()))
                .andExpect(jsonPath("$.roles[0]").value(userResponse.getRoles().get(0)));
    }

    @Test
    public void givenUserRequest_whenUpdateUser_thenUpdatedUserResponseIsReturned() throws Exception {
        Long userId = 1L;
        UserRequest updateUserRequest = new UserRequest(
                "UpdateFirstName",
                "UpdateLastName",
                "update@mail.com",
                "12345678",
                List.of("USER")
        );
        UserResponse expectedUserResponse = new UserResponse(
                userId,
                updateUserRequest.getFirstname(),
                updateUserRequest.getLastname(),
                updateUserRequest.getEmail(),
                updateUserRequest.getRoles()
        );

        given(userService.update(eq(userId), any(UserPatchRequest.class))).willReturn(expectedUserResponse);

        ResultActions response = mockMvc.perform(
                patch("/api/v1/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserRequest))
        );

        response.andExpect(status().isOk())
                //.andDo(print())
                .andExpect(jsonPath("$.id").value(expectedUserResponse.getId()))
                .andExpect(jsonPath("$.firstname").value(expectedUserResponse.getFirstname()))
                .andExpect(jsonPath("$.lastname").value(expectedUserResponse.getLastname()))
                .andExpect(jsonPath("$.email").value(expectedUserResponse.getEmail()))
                .andExpect(jsonPath("$.roles[0]").value(expectedUserResponse.getRoles().get(0)));

    }

    @Test
    public void givenExistingUser_whenDeleteUser_thenNoContentIsReturned() throws Exception {
        Long userId = 1L;

        ResultActions response = mockMvc.perform(
                delete("/api/v1/users/{id}", userId)
        );

        response.andExpect(status().isNoContent());

        verify(userService).delete(userId);
    }

    @Test
    public void givenNonExistingUser_whenDeleteUser_thenNotFoundExceptionIsThrown() throws Exception {
        Long userId = 2L;
        String errorMessage = "User with ID " + userId + " not found";

        doThrow(new EntityNotFoundException(errorMessage)).when(userService).delete(userId);

        ResultActions response = mockMvc.perform(
                delete("/api/v1/users/{id}", userId)
        );

        response.andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value(errorMessage));
    }

}
