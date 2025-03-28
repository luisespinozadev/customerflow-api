package dev.luisespinoza.customerflow.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.luisespinoza.customerflow.security.JwtService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;

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

    @Test
    public void givenUserRequest_whenCreateUser_thenUserResponseIsReturned() throws Exception {
        UserRequest userRequest = new UserRequest(
                "Firstname",
                "Lastname",
                "demo@demo.com",
                "changeme",
                List.of("ADMIN"));
        UserResponse userResponse = new UserResponse(
                2L,
                "Firstname",
                "Lastname",
                "demo@demo.com");
        BDDMockito.given(userService.create(ArgumentMatchers.any(UserRequest.class)))
                .willReturn(userResponse);

        ResultActions response = mockMvc.perform(
                MockMvcRequestBuilders.post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userRequest))
        );

        response.andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstname").value(userRequest.getFirstname()));
    }
}
