package org.example.tests;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RestApi;
import org.example.model.Order;
import org.example.steps.OrderSteps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import java.util.List;

@RunWith(Parameterized.class)
public class OrderParametrizedTest {

    private OrderSteps orderSteps;

    private final List<String> ingredients;
    private final int expectedStatus;
    private final String testName;

    public OrderParametrizedTest(List<String> ingredients, int expectedStatus, String testName) {
        this.ingredients = ingredients;
        this.expectedStatus = expectedStatus;
        this.testName = testName;
    }

    @Parameters(name = "{2}")
    public static Object[][] data() {
        OrderSteps steps = new OrderSteps();  //временный объект для получения списков

        return new Object[][]{
                {steps.getEmptyIngredients(), 400, "0 ингредиентов (ошибка)"},
                {steps.getOnlyBun(), 200, "1 ингредиент (только булка)"},
                {steps.getBunAndSauce(), 200, "2 ингредиента (булка + соус)"},
                {steps.getValidIngredients(), 200, "3 ингредиента (полный набор)"}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = RestApi.BASE_URL;
        orderSteps = new OrderSteps();
    }

    @Test
    public void placeOrderWithDifferentIngredientsTest() {
        Order order = new Order(ingredients);

        // Все запросы без токена — авторизация не требуется
        Response response = orderSteps.placeOrderNoAuth(order);

        if (expectedStatus == 200) {
            orderSteps.verifySuccessfulOrderPlacement(response);
        } else {
            orderSteps.verifyOrderPlacementWithoutIngredients(response);
        }
    }
}