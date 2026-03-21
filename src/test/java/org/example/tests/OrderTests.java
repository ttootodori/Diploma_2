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
    Response userCreationResponse;

    @Before
    public void setUp() {
        RestAssured.baseURI = RestApi.BASE_URL;
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
        User user = userSteps.createRandomUser();
        userCreationResponse = userSteps.createUser(user);
        userSteps.verifyUserCreation(userCreationResponse);
        accessToken = userCreationResponse.path("accessToken");
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Оформление заказа с авторизацией. Проверка успешного создания заказа с валидными ингредиентами.")
    public void placeOrderWithAuthTest() {

        Order order = new Order(orderSteps.getValidIngredients());
        Response orderPlacementResponse = orderSteps.placeOrder(order, accessToken);
        orderSteps.verifySuccessfulOrderPlacement(orderPlacementResponse);
    }

    @Test
    @Description("Оформление заказа без авторизации. Проверка успешного создания заказа с валидными ингредиентами.")
    public void placeOrderWithoutAuthTest() {

        Order order = new Order(orderSteps.getValidIngredients());
        Response orderPlacementResponse = orderSteps.placeOrderNoAuth(order);
        orderSteps.verifySuccessfulOrderPlacement(orderPlacementResponse);
    }

    @Test
    @Description("Оформление заказа с невалидным хэшем ингредиентов. Проверка ошибки: ожидается статус 500.")
    public void placeOrderWithInvalidIngredientHashTest() {

        Order order = new Order(orderSteps.getInvalidIngredients());
        Response orderPlacementResponse = orderSteps.placeOrder(order, accessToken);
        orderSteps.verifyOrderPlacementWithInvalidHash(orderPlacementResponse);
    }

}
