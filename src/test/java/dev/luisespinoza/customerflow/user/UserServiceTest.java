package dev.luisespinoza.customerflow.user;

import dev.luisespinoza.customerflow.role.Role;
import dev.luisespinoza.customerflow.role.RoleRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private UserRequest adminUserRequest;
    private User savedAdminUser;
    private UserResponse expectedAdminUserResponse;
    private Role adminRole;
    private String rawPassword = "12345678";
    private String encodedPassword = "hashedPassword";
    private UserRequest userRequest;
    private User savedUser;
    private UserResponse expectedUserResponse;
    private Role userRole;
    private List<User> userList;
    List<UserResponse> userResponseList;

    @BeforeEach
    public void setUp() {
        // Admin User
        adminRole = new Role(1L, "ADMIN", null, LocalDateTime.now(), LocalDateTime.now());
        adminUserRequest = new UserRequest("AdminFirstName", "AdminLastName", "admin@mail.com", rawPassword, List.of(adminRole.getName()));
        savedAdminUser = User.builder()
                .id(1L)
                .firstname(adminUserRequest.getFirstname())
                .lastname(adminUserRequest.getLastname())
                .email(adminUserRequest.getEmail())
                .password(encodedPassword)
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(adminRole))
                .build();
        expectedAdminUserResponse = new UserResponse(savedAdminUser.getId(), savedAdminUser.getFirstname(), savedAdminUser.getLastname(), savedAdminUser.getEmail(), List.of(savedAdminUser.getRoles().toString()));

        // Regular user
        userRole = new Role(1L, "USER", null, LocalDateTime.now(), LocalDateTime.now());
        userRequest = new UserRequest("UserFirstName", "UserLastName", "user@mail.com", rawPassword, List.of(userRole.getName()));
        savedUser = User.builder()
                .id(2L)
                .firstname(userRequest.getFirstname())
                .lastname(userRequest.getLastname())
                .email(userRequest.getEmail())
                .password(encodedPassword)
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();
        expectedUserResponse = new UserResponse(savedUser.getId(), savedUser.getFirstname(), savedUser.getLastname(), savedUser.getEmail(), List.of(savedUser.getRoles().toString()));

        // List
        userList = List.of(savedAdminUser, savedUser);
        userResponseList = List.of(expectedAdminUserResponse, expectedUserResponse);
    }

    @Test
    public void givenUserRequest_whenCreateUser_thenUserResponseIsReturned() throws Exception {
        given(roleRepository.findByName(adminRole.getName())).willReturn(Optional.of(adminRole));
        given(passwordEncoder.encode(any(String.class))).willReturn(encodedPassword);
        given(userRepository.save(any(User.class))).willReturn(savedAdminUser);

        UserResponse actualUserResponse = userService.create(adminUserRequest);

        assertNotNull(actualUserResponse);
        assertEquals(expectedAdminUserResponse.getId(), actualUserResponse.getId());
        assertEquals(expectedAdminUserResponse.getEmail(), actualUserResponse.getEmail());
    }

    @Test
    public void givenExistingUsers_whenFindAllUsers_thenListOfUsersAreReturned() {
        given(userRepository.findAll()).willReturn(userList);

        List<UserResponse> actualUserResponses = userService.findAll();

        assertNotNull(actualUserResponses);
        assertEquals(userResponseList.size(), actualUserResponses.size());
        assertEquals(userResponseList.get(0).getEmail(), actualUserResponses.get(0).getEmail());
        assertEquals(userResponseList.get(1).getEmail(), actualUserResponses.get(1).getEmail());
    }

    @Test
    public void givenExistingUser_whenFindById_thenUserResponseIsReturned() throws Exception {
        Long userId = savedUser.getId();
        given(userRepository.findById(userId)).willReturn(Optional.of(savedUser));

        UserResponse actualUserResponse = userService.findById(userId);

        assertNotNull(actualUserResponse);
        assertEquals(expectedUserResponse.getId(), actualUserResponse.getId());
        assertEquals(expectedUserResponse.getEmail(), actualUserResponse.getEmail());
        assertEquals(expectedUserResponse.getFirstname(), actualUserResponse.getFirstname());
        assertEquals(expectedUserResponse.getLastname(), actualUserResponse.getLastname());
    }

    @Test
    public void givenNonExistingUser_whenFindById_thenThrowsException() {
        Long nonExistentId = 999L;
        given(userRepository.findById(nonExistentId)).willReturn(Optional.empty());

        EntityNotFoundException thrown = assertThrows(
                EntityNotFoundException.class,
                () -> userService.findById(nonExistentId)
        );

        assertEquals("User with ID " + nonExistentId + " not found", thrown.getMessage());
    }
    @Test
    public void givenUserRequest_whenUpdateUser_thenUserResponseIsReturned() {
        Long userId = savedAdminUser.getId();
        UserPatchRequest updateRequest = new UserPatchRequest(
                "UpdatedName", "UpdatedLastName", "updated@mail.com", rawPassword, List.of(userRole.getName())
        );
        User updatedUser = User.builder()
                .id(userId)
                .firstname(updateRequest.getFirstname())
                .lastname(updateRequest.getLastname())
                .email(updateRequest.getEmail())
                .password(encodedPassword)
                .accountLocked(false)
                .enabled(true)
                .roles(List.of(userRole))
                .build();
        UserResponse expectedResponse = new UserResponse(
                userId,
                updateRequest.getFirstname(),
                updateRequest.getLastname(),
                updateRequest.getEmail(),
                updateRequest.getRoles()
        );
        // Mocks
        given(userRepository.findById(userId)).willReturn(Optional.of(savedAdminUser));
        given(passwordEncoder.encode(updateRequest.getPassword())).willReturn(encodedPassword);
        given(roleRepository.findByName(userRole.getName())).willReturn(Optional.of(userRole));
        given(userRepository.save(any(User.class))).willReturn(updatedUser);

        UserResponse actualResponse = userService.update(userId, updateRequest);

        assertNotNull(actualResponse);
        assertEquals(expectedResponse.getId(), actualResponse.getId());
        assertEquals(expectedResponse.getFirstname(), actualResponse.getFirstname());
        assertEquals(expectedResponse.getLastname(), actualResponse.getLastname());
        assertEquals(expectedResponse.getEmail(), actualResponse.getEmail());
    }

    @Test
    public void givenExistingUser_whenDeleteUser_thenNoContentIsReturned() {
        Long userId = savedUser.getId();
        given(userRepository.findById(userId)).willReturn(Optional.of(savedUser));

        userService.delete(userId);

        verify(userRepository).delete(savedUser);
    }

    @Test
    public void givenNonExistingUser_whenDeleteUser_thenNotFoundExceptionIsThrown() {
        Long userId = savedUser.getId();
        given(userRepository.findById(userId)).willReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> userService.delete(userId));

        verify(userRepository, never()).delete(any());
    }


}
