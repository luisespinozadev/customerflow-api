package dev.luisespinoza.customerflow.user;

import dev.luisespinoza.customerflow.role.Role;
import dev.luisespinoza.customerflow.role.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    public UserResponse create(
            UserRequest request
    ) throws Exception {

        List<Role> userRoles = new ArrayList<>();

        for (String roleName : request.getRoles()) {
            Role userRole = roleRepository.findByName(roleName)
                    .orElseThrow(() -> new IllegalStateException("Role " + roleName + " not found"));
            userRoles.add(userRole);
        }

        User user = User.builder()
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .accountLocked(false)
                .enabled(true)
                .roles(userRoles)
                .build();

        user = userRepository.save(user);
        return mapper.map(user, UserResponse.class);
    }

}
