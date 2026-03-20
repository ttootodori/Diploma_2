package org.example.tests;

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

    UserSteps userSteps;
    String accessToken;

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
    public void logInExistingUserTest() {
        User user = userSteps.createRandomUser();
        Response createResponse = userSteps.createUser(user);
        userSteps.verifyUserCreation(createResponse);

        Response loginResponse = userSteps.loginUser(user);
        userSteps.verifyLoginSuccess(loginResponse, user);
        accessToken = userSteps.getAccessToken();
    }

    @Test
    public void loginExistingUserWithWrongPasswordTest() {
        User user = userSteps.createRandomUser();
        Response createResponse = userSteps.createUser(user);
        userSteps.verifyUserCreation(createResponse);

        User userForLogin = new User(user.getEmail(), "0000");
        Response response = userSteps.loginUser(userForLogin);
        userSteps.verifyLoginFailed(response);
        accessToken = userSteps.getAccessToken();
    }

    @Test
    public void loginNonExistingUserTest() {
        User userForLogin = new User("0000", "0000");
        Response response = userSteps.loginUser(userForLogin);
        userSteps.verifyLoginFailed(response);
    }

}
