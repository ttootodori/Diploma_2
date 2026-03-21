package org.example.tests;

import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.example.RestApi;
import org.example.model.Order;
import org.example.model.User;
import org.example.steps.OrderSteps;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.example.steps.UserSteps;
import static org.apache.http.HttpStatus.*;

import java.util.List;

@RunWith(Parameterized.class)
public class OrderParametrizedTest {

    private OrderSteps orderSteps;
    private UserSteps userSteps;
    String accessToken;

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
                {steps.getEmptyIngredients(), SC_BAD_REQUEST, "0 ингредиентов (ошибка)"},
                {steps.getOnlyBun(), SC_OK, "1 ингредиент (только булка)"},
                {steps.getBunAndSauce(), SC_OK, "2 ингредиента (булка + соус)"},
                {steps.getValidIngredients(), SC_OK, "3 ингредиента (полный набор)"}
        };
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = RestApi.BASE_URL;
        userSteps = new UserSteps();
        orderSteps = new OrderSteps();
        User user = userSteps.createRandomUser();
        Response userCreationResponse = userSteps.createUser(user);
        userSteps.verifyUserCreation(userCreationResponse);
        accessToken = userSteps.getAccessToken();
    }

    @After
    public void cleanUp() {
        if (accessToken != null) {
            userSteps.deleteUser(accessToken);
        }
    }

    @Test
    @Description("Оформление заказа с разным количеством ингредиентов. " +
            "Проверка успешного создания заказа с 1, 2 и 3 ингредиентами, " +
            "а также ошибки при заказе без ингредиентов.")
    public void placeOrderWithDifferentIngredientsTest() {

        Order order = new Order(ingredients);
        Response orderPlacementResponse = orderSteps.placeOrder(order, userSteps.getAccessToken());

        if (expectedStatus == SC_OK) {
            orderSteps.verifySuccessfulOrderPlacement(orderPlacementResponse);
        } else {
            orderSteps.verifyOrderPlacementWithoutIngredients(orderPlacementResponse);
        }
    }
}