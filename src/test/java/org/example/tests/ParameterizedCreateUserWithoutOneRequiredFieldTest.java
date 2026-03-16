package org.example.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RestApi;
import org.example.client.UserClient;
import org.example.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;

@RunWith(Parameterized.class)
public class ParameterizedCreateUserWithoutOneRequiredFieldTest {

    private String email;
    private String password;
    private String name;

    public ParameterizedCreateUserWithoutOneRequiredFieldTest(String email, String password, String name) {
        this.email = email;
        this.password = password;
        this.name = name;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = RestApi.BASE_URL;
    }

    @Parameterized.Parameters
    public static Object[][] data() {
        return new Object[][]{
                {null, "password123", "Vasya"},
                {"email@ya.ru", null, "Vasya"},
                {"email@ya.ru", "password123", null}
        };
    }

    @Test
    public void createUserWithoutField() {
        User user = new User(email, password, name);
        Response response = new UserClient().create(user);

        assertEquals(403, response.statusCode());
        assertEquals(false, response.path("success"));
    }
}