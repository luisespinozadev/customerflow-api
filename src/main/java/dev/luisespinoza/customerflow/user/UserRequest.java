package dev.luisespinoza.customerflow.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserRequest {

    //@NotBlank(message = "The firstname cannot be empty")
    private String firstname;
    //@NotBlank(message = "The lastname cannot be empty")
    private String lastname;
    @NotBlank(message = "The email cannot be empty")
    private String email;
    @NotBlank(message = "The password cannot be empty")
    private String password;
    @NotEmpty(message = "The roles cannot be empty")
    private List<String> roles;
}
