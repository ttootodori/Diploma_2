package org.example.tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RestApi;
import org.example.model.Order;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.example.steps.UserSteps;

import java.util.List;

@RunWith(Parameterized.class)
public class OrderParametrizedTest {

    private OrderSteps orderSteps;
    private UserSteps userSteps;

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
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
    }

    @Test
    @Description("Проверка оформления заказа с разным количеством ингредиентов.")
    public void placeOrderWithDifferentIngredientsTest() {

        User user = userSteps.createRandomUser();
        Response response = userSteps.createUser(user);
        userSteps.verifyUserCreation(response);

        Order order = new Order(ingredients);
        Response response1 = orderSteps.placeOrder(order, userSteps.getAccessToken());

        if (expectedStatus == 200) {
            orderSteps.verifySuccessfulOrderPlacement(response1);
        } else {
            orderSteps.verifyOrderPlacementWithoutIngredients(response1);
        }
    }
}