package org.example.tests;

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
import io.restassured.http.ContentType;

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

    @Test
    public void createUniqueUserTest() {
        User user = User.getRandom();
        Response response = new UserClient().create(user);

        assertEquals(200, response.statusCode());
        assertEquals(true, response.path("success"));
        assertNotNull(response.path("accessToken"));
        accessToken = response.path("accessToken");
    }

    @Test
    public void createExistingUserTest() {

        User user = User.getRandom();
        Response firstResponse = new UserClient().create(user);
        assertEquals(200, firstResponse.statusCode());
        assertEquals(true, firstResponse.path("success"));
        accessToken = firstResponse.path("accessToken");

        Response secondResponse = new UserClient().create(user);
        assertEquals(403, secondResponse.statusCode());
        assertEquals(false, secondResponse.path("success"));
    }

    @Test
    public void logInExistingUser() {
        //создание пользователя
        User user = User.getRandom();
        Response createResponse = new UserClient().create(user);
        assertEquals(200, createResponse.statusCode());
        accessToken = createResponse.path("accessToken");

        //логин пользователя
        Response loginResponse = new UserClient().login(user);
        assertEquals(200, loginResponse.statusCode());

        //проверка логина
        assertEquals(true, loginResponse.path("success"));
        assertNotNull(loginResponse.path("accessToken"));
        assertNotNull(loginResponse.path("refreshToken"));

        //проверка, что вернулись правильные данные пользователя
        assertEquals(user.getEmail(), loginResponse.path("user.email"));
        assertEquals(user.getName(), loginResponse.path("user.name"));

    }

}
