package com.codecool.kgp.controller;

import com.codecool.kgp.config.SpringSecurityConfig;
import com.codecool.kgp.controller.dto.UserChallengeSimpleDto;
import com.codecool.kgp.controller.dto.UserDto;
import com.codecool.kgp.controller.dto.UserRequestDto;
import com.codecool.kgp.entity.User;
import com.codecool.kgp.entity.UserChallenge;
import com.codecool.kgp.mappers.UserMapper;
import com.codecool.kgp.config.WithMockCustomUser;
import com.codecool.kgp.repository.UserRepository;
import com.codecool.kgp.service.CustomUserDetailsService;
import com.codecool.kgp.service.UserService;
import com.google.gson.Gson;
import org.instancio.Instancio;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.codecool.kgp.config.SpringSecurityConfig.ADMIN;
import static com.codecool.kgp.config.SpringSecurityConfig.USER;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.instancio.Select.field;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Import({SpringSecurityConfig.class, CustomUserDetailsService.class})
@WebMvcTest(UserController.class)
class UserControllerTest {

    private final Gson gson = new Gson();

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;

    @MockBean
    private UserMapper userMapper;

    @MockBean
    private UserRepository userRepository;


    @Test
    @WithMockCustomUser(username = "admin", role = ADMIN)
    void getUsers_shouldReturnUsers() throws Exception {
        //given:
        List<User> users = Instancio.ofList(User.class)
                .size(2)
                .setBlank(field(User::getUserChallengesSet))
                .set(field(User::getUserChallengesSet), Instancio.ofSet(UserChallenge.class).size(1).create())
                .create();
        users.forEach(user -> user.getUserChallengesSet().stream().findFirst().ifPresent(uch-> uch.setUser(user)));

        Mockito.when(userService.getUsers()).thenReturn(users);
        users.forEach(this::mockUserMapper);

        //when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/"));

        //then:
        response.andExpect(status().isOk());
        response.andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.size()").value(2));
        response.andExpect(jsonPath("$[0].id").value(users.get(0).getId().toString()))
                .andExpect(jsonPath("$[0].login").value(users.get(0).getLogin()))
                .andExpect(jsonPath("$[0].name").value(users.get(0).getName()))
                .andExpect(jsonPath("$[0].email").value(users.get(0).getEmail()))
                .andExpect(jsonPath("$[0].coordinates").value(containsInAnyOrder(users.get(0).getCoordinatesArray())))
                .andExpect(jsonPath("$[0].phone").value(users.get(0).getPhone()))
                .andExpect(jsonPath("$[0].newsletter").value(users.get(0).getNewsletter()))
                .andExpect(jsonPath("$[0].createAt").value(users.get(0).getCreatedAt().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].deleteAt").value(users.get(0).getDeletedAt().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$[0].role").value(users.get(0).getRole().toString()))
                .andExpect(jsonPath("$[0].userChallengesSetSimplified").isNotEmpty());
    }

    @Test
    @WithMockCustomUser(username = "admin", role = ADMIN)
    void getUserById_shouldReturnUser() throws Exception {
        //given:
        User user = Instancio.of(User.class)
                .set(field(User::getUserChallengesSet), Instancio.ofSet(UserChallenge.class).size(1).create())
                .create();

        user.getUserChallengesSet().stream().findFirst().ifPresent(uch->uch.setUser(user));

        Mockito.when(userService.getUser(user.getId())).thenReturn(user);
        mockUserMapper(user);

        //when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/{id}", user.getId()));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.coordinates").value(containsInAnyOrder(user.getCoordinatesArray())))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.newsletter").value(user.getNewsletter()))
                .andExpect(jsonPath("$.createAt").value(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                )
                .andExpect(jsonPath("$.deleteAt").value(user.getDeletedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.role").value(user.getRole().toString()))
                .andExpect(jsonPath("$.userChallengesSetSimplified").isNotEmpty());
    }

    @Test
    @WithMockCustomUser(username = "user", role = USER, id = "123e4567-e89b-12d3-a456-426614174000")
    void getUser_shouldReturnUser() throws Exception {
        //given:
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        User user = Instancio.of(User.class)
                .set(field(User::getUserChallengesSet), Instancio.ofSet(UserChallenge.class).size(1).create()).create();

        user.getUserChallengesSet().stream().findFirst().ifPresent(uch->uch.setUser(user));

        Mockito.when(userService.getUser(id)).thenReturn(user);
        mockUserMapper(user);

        //when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/me"));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(user.getId().toString()))
                .andExpect(jsonPath("$.login").value(user.getLogin()))
                .andExpect(jsonPath("$.name").value(user.getName()))
                .andExpect(jsonPath("$.email").value(user.getEmail()))
                .andExpect(jsonPath("$.coordinates").value(containsInAnyOrder(user.getCoordinatesArray())))
                .andExpect(jsonPath("$.phone").value(user.getPhone()))
                .andExpect(jsonPath("$.newsletter").value(user.getNewsletter()))
                .andExpect(jsonPath("$.createAt").value(user.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.deleteAt").value(user.getDeletedAt().format(DateTimeFormatter.ISO_DATE_TIME)))
                .andExpect(jsonPath("$.role").value(user.getRole().toString()))
                .andExpect(jsonPath("$.userChallengesSetSimplified").isNotEmpty());
    }

    @Test
    @WithMockCustomUser(username = "user", role = USER, id = "123e4567-e89b-12d3-a456-426614174000")
    void getUserScore_shouldReturnScore() throws Exception {
        //given:
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        int score = 100;
        Mockito.when(userService.getUserScore(id)).thenReturn(score);

        //when:
        ResultActions response = mockMvc.perform(get("/api/v1/users/me/score"));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$").value(score));
    }

    @Test
    @WithMockCustomUser(username = "user", role = USER, id = "123e4567-e89b-12d3-a456-426614174000")
    void updateUser_shouldUpdateUser() throws Exception {
        //given:
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");
        UserRequestDto dto = Instancio.of(UserRequestDto.class).create();
        User updatedUser = Instancio.of(User.class)
                .set(field(User::getUserChallengesSet), Instancio.ofSet(UserChallenge.class).size(1).create()).create();

        Mockito.when(userService.updateUser(id, dto)).thenReturn(updatedUser);
        mockUserMapper(updatedUser);

        //when:
        var response = mockMvc.perform(put("/api/v1/users/me")
                .contentType("application/json")
                .content(gson.toJson(dto)));

        //then:
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(updatedUser.getId().toString()))
                .andExpect(jsonPath("$.login").value(updatedUser.getLogin()))
                .andExpect(jsonPath("$.name").value(updatedUser.getName()))
                .andExpect(jsonPath("$.email").value(updatedUser.getEmail()))
                .andExpect(jsonPath("$.coordinates").value(containsInAnyOrder(updatedUser.getCoordinatesArray())))
                .andExpect(jsonPath("$.phone").value(updatedUser.getPhone()))
                .andExpect(jsonPath("$.newsletter").value(updatedUser.getNewsletter()))
                .andExpect(jsonPath("$.createAt").value(updatedUser.getCreatedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.deleteAt").value(updatedUser.getDeletedAt().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)))
                .andExpect(jsonPath("$.role").value(updatedUser.getRole().toString()))
                .andExpect(jsonPath("$.userChallengesSetSimplified").isNotEmpty());
        ArgumentCaptor<UserRequestDto> captor = ArgumentCaptor.forClass(UserRequestDto.class);
        Mockito.verify(userService).updateUser(Mockito.eq(id), captor.capture());
        Assertions.assertEquals(captor.getValue(), dto);
    }

    @Test
    @WithMockCustomUser(username = "user", role = USER, id = "123e4567-e89b-12d3-a456-426614174000")
    void deleteUser_shouldDeleteUser() throws Exception {
        //given:
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        //when:
        var response = mockMvc.perform(delete("/api/v1/users/me"));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(userService).softDeleteUser(id);
    }

    @Test
    @WithMockCustomUser(username = "user", role = USER, id = "123e4567-e89b-12d3-a456-426614174000")
    void hardDeleteUser_shouldDeleteUser() throws Exception {
        //given:
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        //when:
        var response = mockMvc.perform(delete("/api/v1/users/me").queryParam("hardDelete", "true"));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(userService).deleteUser(id);
    }

    @Test
    @WithMockCustomUser(username = "admin", role = ADMIN)
    void deleteUserById_shouldDeleteUser() throws Exception {
        //given:
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        //when:
        var response = mockMvc.perform(delete("/api/v1/users/{id}", id));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(userService).softDeleteUser(id);
    }

    @Test
    @WithMockCustomUser(username = "admin", role = ADMIN)
    void hardDeleteUserById_shouldDeleteUser() throws Exception {
        //given:
        UUID id = UUID.fromString("123e4567-e89b-12d3-a456-426614174000");

        //when:
        var response = mockMvc.perform(delete("/api/v1/users/{id}", id).queryParam("hardDelete", "true"));

        //then:
        response.andExpect(status().isOk());
        Mockito.verify(userService).deleteUser(id);
    }


    private void mockUserMapper(User user) {
        Mockito.when(userMapper.mapEntityToDto(user)).thenReturn(new UserDto(
                user.getId(),
                user.getLogin(),
                user.getName(),
                user.getEmail(),
                user.getCoordinatesArray(),
                user.getPhone(),
                user.getNewsletter(),
                user.getCreatedAt(),
                user.getDeletedAt(),
                user.getRole().toString(),
                user.getUserChallengesSet().stream().map(uch -> new UserChallengeSimpleDto(
                        uch.getId(),
                        uch.getUser().getId(),
                        uch.getChallenge().getName(),
                        uch.getStartedAt(),
                        uch.getFinishedAt(),
                        uch.getScore()
                )).collect(Collectors.toSet()))
        );
    }
}