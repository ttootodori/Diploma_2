package org.example.tests;

import io.qameta.allure.Description;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;

import org.example.RestApi;
import org.example.steps.UserSteps;
import org.example.model.User;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

public class UserCreationTests {

    private String accessToken;
    private UserSteps userSteps;

    @Before
    public void setUp() {
        RestAssured.baseURI = RestApi.BASE_URL;
        RestAssured.filters(new AllureRestAssured());
        userSteps = new UserSteps();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Создание уникального пользователя. Проверка успешной регистрации: статус 200, success: true, наличие accessToken.")
    public void createUniqueUserTest() {
        User user = userSteps.createRandomUser();
        Response response = userSteps.createUser(user);
        userSteps.verifyUserCreation(response);
        accessToken = userSteps.getAccessToken();
    }

    @Test
    @Description("Создание уже существующего пользователя. Проверка ошибки: статус 403, success: false.")
    public void createExistingUserTest() {
        User user = userSteps.createRandomUser();
        Response firstResponse = userSteps.createUser(user);
        userSteps.verifyUserCreation(firstResponse);

        Response secondResponse = userSteps.createUser(user);
        userSteps.verifyUserCreationError(secondResponse);
    }
}