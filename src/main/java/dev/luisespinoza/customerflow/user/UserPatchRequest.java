package dev.luisespinoza.customerflow.user;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class UserPatchRequest {

    private String firstname;
    private String lastname;
    private String email;
    private String password;
    private List<String> roles;
}
