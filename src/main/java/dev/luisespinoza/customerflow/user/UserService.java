package dev.luisespinoza.customerflow.user;

import dev.luisespinoza.customerflow.role.Role;
import dev.luisespinoza.customerflow.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

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
        return mapToResponse(user);
    }

    public List<UserResponse> findAll() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .toList();
        return users;
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
        return mapToResponse(user);
    }

    public UserResponse update(Long id, UserPatchRequest userRequest) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));

        if (userRequest.getFirstname() != null) {
            user.setFirstname(userRequest.getFirstname());
        }
        if (userRequest.getLastname() != null) {
            user.setLastname(userRequest.getLastname());
        }
        if (userRequest.getEmail() != null) {
            user.setEmail(userRequest.getEmail());
        }
        if (userRequest.getPassword() != null) {
            user.setPassword(passwordEncoder.encode(userRequest.getPassword()));
        }
        if (userRequest.getRoles() != null) {
            List<Role> roles = new ArrayList<>();
            for (String roleName : userRequest.getRoles()) {
                Role userRole = roleRepository.findByName(roleName)
                        .orElseThrow(() -> new EntityNotFoundException("Role " + roleName + " not found"));
                roles.add(userRole);
            }
            user.setRoles(roles);
        }

        user = userRepository.save(user);
        return mapToResponse(user);

    }

    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));

        userRepository.delete(user);
    }

    private UserResponse mapToResponse(User user) {
        List<String> roleNames = Optional.ofNullable(user.getRoles())
                .orElse(Collections.emptyList())
                .stream()
                .map(Role::getName)
                .toList();

        return new UserResponse(
                user.getId(),
                user.getFirstname(),
                user.getLastname(),
                user.getEmail(),
                roleNames
        );
    }

}
