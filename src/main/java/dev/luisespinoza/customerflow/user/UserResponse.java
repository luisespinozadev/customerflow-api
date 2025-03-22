package dev.luisespinoza.customerflow.user;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserResponse {
    private Long id;
    private String firstname;
    private String lastname;
    private String email;
}
