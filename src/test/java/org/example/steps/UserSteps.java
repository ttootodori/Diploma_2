package org.example.steps;

import io.qameta.allure.Step;
import org.example.client.UserClient;
import org.example.model.User;
import io.restassured.response.Response;
import static org.junit.Assert.*;

public class UserSteps {

    private UserClient userClient = new UserClient();
    private String accessToken;

    @Step("Создание данных для рандомного пользователя")
    public User createRandomUser() {
        return User.getRandom();
    }

    @Step("Создание пользователя через API")
    public Response createUser(User user) {
        return new UserClient().create(user);
    }

    @Step("Логин пользователя {user.email}")
    public Response loginUser(User user) {
        return new UserClient().login(user);
    }

    @Step("Проверка успешного создания пользователя")
    public void verifyUserCreation(Response response) {
        assertEquals(200, response.statusCode());
        assertEquals(true, response.path("success"));
        assertNotNull(response.path("accessToken"));
        accessToken = response.path("accessToken");
    }

    @Step("Проверка ошибки при создании существующего пользователя")
    public void verifyUserCreationError(Response response) {
        assertEquals(403, response.statusCode());
        assertEquals(false, response.path("success"));
    }

    @Step("Проверка успешного логина для пользователя {user.email}")
    public void verifyLoginSuccess(Response response, User user) {
        assertEquals(200, response.statusCode());
        assertEquals(true, response.path("success"));
        assertNotNull(response.path("accessToken"));
        assertNotNull(response.path("refreshToken"));
        assertEquals(user.getEmail(), response.path("user.email"));
        assertEquals(user.getName(), response.path("user.name"));
    }

    @Step("Проверка ошибки логина с неверными данными")
    public void verifyLoginFailed(Response response) {
        assertEquals(401, response.statusCode());
        assertEquals(false, response.path("success"));
        assertEquals("email or password are incorrect", response.path("message"));
    }

    @Step("Удаление пользователя с токеном {accessToken}")
    public void deleteUser(String accessToken) {

        Response response = userClient.delete(accessToken);
        assertEquals(202, response.statusCode());

    }



    @Step
    public String getAccessToken() { return accessToken; }

}