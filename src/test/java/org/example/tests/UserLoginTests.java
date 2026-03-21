package org.example.tests;

import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RestApi;
import org.example.model.User;
import org.example.steps.UserSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class UserLoginTests {

    User user;
    UserSteps userSteps;
    String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = RestApi.BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        userSteps = new UserSteps();

        user = userSteps.createRandomUser();
        Response createResponse = userSteps.createUser(user);
        userSteps.verifyUserCreation(createResponse);
        accessToken = userSteps.getAccessToken();
    }
    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Логин существующего пользователя. Проверка успешного входа: статус 200, успешный ответ, токены в ответе.")
    public void logInExistingUserTest() {

        Response loginResponse = userSteps.loginUser(user);
        userSteps.verifyLoginSuccess(loginResponse, user);

    }

    @Test
    @Description("Логин с неверным паролем. Проверка ошибки авторизации: статус 401, сообщение о неверном пароле.")
    public void loginExistingUserWithWrongPasswordTest() {

        User userForLogin = new User(user.getEmail(), "0000");
        Response loginResponse = userSteps.loginUser(userForLogin);
        userSteps.verifyLoginFailed(loginResponse);

    }

    @Test
    @Description("Логин несуществующего пользователя. Проверка ошибки авторизации: статус 401, сообщение о неверных данных.")
    public void loginNonExistingUserTest() {

        User userForLogin = new User("0000", "0000");
        Response loginResponse = userSteps.loginUser(userForLogin);
        userSteps.verifyLoginFailed(loginResponse);
    }

}
