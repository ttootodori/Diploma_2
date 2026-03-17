package org.example.tests;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;
import static org.junit.Assert.*;

import org.example.RestApi;
import org.example.client.UserClient;
import org.example.model.User;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

public class UserCreationAndLoginTests {

    private String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = RestApi.BASE_URL;
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            given()
                    .header("Authorization", accessToken)
                    .delete(RestApi.USER_PATH);
        }
    }

    @Step("Создание рандомного пользователя")
    private User createRandomUser() {
        return User.getRandom();
    }

    @Step("Создание пользователя через API")
    private Response createUser(User user) {
        return new UserClient().create(user);
    }

    @Step("Логин пользователя {user.email}")
    private Response loginUser(User user) {
        return new UserClient().login(user);
    }

    @Step("Проверка успешного создания пользователя")
    private void verifyUserCreation(Response response) {
        assertEquals(200, response.statusCode());
        assertEquals(true, response.path("success"));
        assertNotNull(response.path("accessToken"));
        accessToken = response.path("accessToken");
    }

    @Step("Проверка ошибки при создании существующего пользователя")
    private void verifyUserCreationError(Response response) {
        assertEquals(403, response.statusCode());
        assertEquals(false, response.path("success"));
    }

    @Step("Проверка успешного логина для пользователя {user.email}")
    private void verifyLoginSuccess(Response response, User user) {
        assertEquals(200, response.statusCode());
        assertEquals(true, response.path("success"));
        assertNotNull(response.path("accessToken"));
        assertNotNull(response.path("refreshToken"));
        assertEquals(user.getEmail(), response.path("user.email"));
        assertEquals(user.getName(), response.path("user.name"));
    }

    @Step("Проверка ошибки логина с неверными данными")
    private void verifyLoginFailed(Response response) {
        assertEquals(401, response.statusCode());
        assertEquals(false, response.path("success"));
        assertEquals("email or password are incorrect", response.path("message"));
    }

    @Test
    public void createUniqueUserTest() {
        User user = createRandomUser();
        Response response = createUser(user);
        verifyUserCreation(response);
    }

    @Test
    public void createExistingUserTest() {
        User user = createRandomUser();
        Response firstResponse = createUser(user);
        verifyUserCreation(firstResponse);

        Response secondResponse = createUser(user);
        verifyUserCreationError(secondResponse);
    }

    @Test
    public void logInExistingUserTest() {
        User user = createRandomUser();
        Response createResponse = createUser(user);
        verifyUserCreation(createResponse);

        Response loginResponse = loginUser(user);
        verifyLoginSuccess(loginResponse, user);
    }

    @Test
    public void logInExistingUserWithWrongPasswordTest() {
        User user = createRandomUser();
        Response createResponse = createUser(user);
        verifyUserCreation(createResponse);

        User userForLogin = new User(user.getEmail(), "0000");
        Response response = loginUser(userForLogin);
        verifyLoginFailed(response);

    }

}
