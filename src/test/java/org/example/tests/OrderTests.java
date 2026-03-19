package org.example.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.qameta.allure.Description;

import org.example.RestApi;
import org.example.steps.UserSteps;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.example.model.Order;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

public class OrderTests {

    UserSteps userSteps;
    OrderSteps orderSteps;
    String accessToken;

    @Before
    public void setUp() {
        RestAssured.baseURI = RestApi.BASE_URL;
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Проверка оформления заказа с авторизацией.")
    public void placeOrderWithAuth() {
        User user = userSteps.createRandomUser();
        Response response = userSteps.createUser(user);
        userSteps.verifyUserCreation(response);
        accessToken = response.path("accessToken");

        Order order = new Order(orderSteps.getValidIngredients());
        Response response1 = orderSteps.placeOrder(order, accessToken);
        orderSteps.verifySuccessfulOrderPlacement(response1);
    }

    @Test
    @Description("Проверка оформления заказа без авторизации.")
    public void placeOrderWithoutAuth() {

        Order order = new Order(orderSteps.getValidIngredients());
        Response response1 = orderSteps.placeOrderNoAuth(order);
        orderSteps.verifySuccessfulOrderPlacement(response1);
    }

    @Test
    @Description("Проверка, что при попытке создать заказ с невалидным хэшем, вернётся ошибка.")
    public void placeOrderWithInvalidIngredientHash() {

        User user = userSteps.createRandomUser();
        Response response = userSteps.createUser(user);
        userSteps.verifyUserCreation(response);
        accessToken = response.path("accessToken");

        Order order = new Order(orderSteps.getInvalidIngredients());
        Response response1 = orderSteps.placeOrder(order, accessToken);
        orderSteps.verifyOrderPlacementWithInvalidHash(response1);

    }

}
