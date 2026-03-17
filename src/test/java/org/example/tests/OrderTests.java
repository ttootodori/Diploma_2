package org.example.tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import static io.restassured.RestAssured.*;

import org.example.RestApi;
import org.example.steps.UserSteps;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.example.model.Order;
import org.junit.After;
import org.junit.Test;
import org.junit.Before;

import java.util.List;

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
    public void placeOrderWithoutAuth() {

        Order order = new Order(orderSteps.getValidIngredients());
        Response response1 = orderSteps.placeOrderNoAuth(order);
        orderSteps.verifySuccessfulOrderPlacement(response1);
    }

    @Test
    public void placeOrderWithInvalidIngredientHash() {

        Order order = new Order(orderSteps.getInvalidIngredients());
        Response response = orderSteps.placeOrderNoAuth(order);
        orderSteps.verifyOrderPlacementWithInvalidHash(response);
    }

}
