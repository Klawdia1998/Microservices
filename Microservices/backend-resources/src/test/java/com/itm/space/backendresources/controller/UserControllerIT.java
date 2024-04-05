package com.itm.space.backendresources.controller;

import com.itm.space.backendresources.BaseIntegrationTest;
import com.itm.space.backendresources.api.request.UserRequest;
import com.itm.space.backendresources.service.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.ws.rs.core.MediaType;
import java.util.List;
import java.util.UUID;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WithMockUser(username = "demo", roles = "MODERATOR")
public class UserControllerIT extends BaseIntegrationTest {

    @Autowired
    UserService userService;
    @Autowired
    Keycloak keycloak;

    @AfterEach
    void cleanUp() {
        List<UserRepresentation> userList = keycloak
                .realm("ITM").users().search("Klawdia24");
        if (!(userList.isEmpty())) {
            UserRepresentation userRepresentation = userList.get(0);
            keycloak.realm("ITM").users().get(userRepresentation.getId()).remove();
        }
    }

    @Test
    @DisplayName("Создание пользователя с валидным запросом должно быть успешным")
    void createUserWithValidRequestEntity() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "Klawdia24",
                          "email": "klawdia@exmp.com",
                          "password": "password123",
                          "firstName": "Klawdia",
                          "lastName": "Kolo"
                        }
                        """);

        mvc.perform(requestBuilder).andExpect(status().isOk());
    }

    @Test
    @DisplayName("При создании пользователя введен не корректный запрос")
    void createUserWithInvalidRequestEntity() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.post("/api/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "username": "Klawdia24",
                          "email": "xxxxxx",
                          "password": "password123",
                          "firstName": "Klawdia",
                          "lastName": "Kolo"
                        }
                        """);

        mvc.perform(requestBuilder).andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Проверка получения пользователя по ID с корректным ответом")
    void getUserByIdReturnValidResponseEntity() throws Exception {
        userService.createUser(new UserRequest("Klawdia24", "klawdia@exmp.com", "password123", "Klawdia", "Kolo"));
        String newUserRequestUUID = keycloak.realm("ITM").users().search("Klawdia24").get(0).getId();
        
        var requestBuilder = MockMvcRequestBuilders.get("/api/users/" + newUserRequestUUID);
        
        mvc.perform(requestBuilder)
                .andExpectAll(
                        status().isOk(),
                        content().contentType(MediaType.APPLICATION_JSON),
                        content().json("""
                                {
                                "firstName": "Klawdia",
                                "lastName": "Kolo",
                                "email": "klawdia@exmp.com",
                                "roles": ["default-roles-itm"],
                                "groups": []
                                }
                                """)
                );
    }

    @Test
    @DisplayName("Проверка, что при попытке получить пользователя по неверному ID возвращается статус ошибки 500")
    public void getUserByIdReturnsInValidResponseEntity() throws Exception {
        String testUserUUID = String.valueOf(UUID.randomUUID());
        
        var requestBuilder = MockMvcRequestBuilders.get("/api/users/" + testUserUUID);
        
        mvc.perform(requestBuilder).andExpect(status().is(500));
    }

    @Test
    @DisplayName("Проверка доступа к методу `hello` для пользователя с ролью `ROLE_MODERATOR`, ожидается статус 200 OK")
    void helloTest() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/api/users/hello");
        
        mvc.perform(requestBuilder)
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(roles = "USER")
    @DisplayName("Проверка, что доступ к методу `hello` для пользователя без роли `ROLE_MODERATOR` запрещен, ожидается статус 403 Forbidden")
    void helloWithoutAuthorities() throws Exception {
        var requestBuilder = MockMvcRequestBuilders.get("/api/users/hello");
        
        mvc.perform(requestBuilder)
                .andExpect(status().isForbidden());
    }
}



