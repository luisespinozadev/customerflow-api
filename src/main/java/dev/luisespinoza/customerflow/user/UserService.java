package dev.luisespinoza.customerflow.user;

import dev.luisespinoza.customerflow.role.Role;
import dev.luisespinoza.customerflow.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
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

    public List<UserResponse> findAll() {
        List<UserResponse> users = userRepository.findAll()
                .stream()
                .map(user -> mapper.map(user, UserResponse.class))
                .toList();
        return users;
    }

    public UserResponse findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with ID " + id + " not found"));
        return mapper.map(user, UserResponse.class);
    }

    public UserResponse update(Long id, UserRequest userRequest) {
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
        return mapper.map(user, UserResponse.class);

    }

}
