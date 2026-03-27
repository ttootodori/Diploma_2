package org.example.tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RestApi;
import org.example.client.UserClient;
import org.example.model.User;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;

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

    @Parameterized.Parameters(name = "Тест с полями: email={0}, password={1}, name={2}")
    public static Object[][] data() {
        return new Object[][]{
                {null, "password123", "Vasya"},
                {"email@ya.ru", null, "Vasya"},
                {"email@ya.ru", "password123", null}
        };
    }

    @Test
    @Description("Создание пользователя с отсутствием одного из обязательных полей (email, password или name). " +
            "Ожидается статус 403, success: false, сообщение об ошибке.")
    public void createUserWithoutRequiredFieldTest() {
        User user = new User(email, password, name);
        Response userCreationResponse = new UserClient().create(user);

        assertEquals(SC_FORBIDDEN, userCreationResponse.statusCode());
        assertEquals(false, userCreationResponse.path("success"));
        assertEquals("Email, password and name are required fields", userCreationResponse.path("message"));
    }
}