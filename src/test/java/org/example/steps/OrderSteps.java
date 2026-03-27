package org.example.steps;

import io.qameta.allure.Step;
import org.example.client.OrderClient;
import org.example.model.Order;
import io.restassured.response.Response;
import static org.apache.http.HttpStatus.*;

import java.util.*;

import static org.junit.Assert.*;


public class OrderSteps {

    private String accessToken;

    @Step("Создать список валидных ингредиентов.")
    public List<String> getValidIngredients() {
        return Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",  // булка
                "61c0c5a71d1f82001bdaaa72",  // соус
                "61c0c5a71d1f82001bdaaa6f"   // начинка
        );
    }
    @Step("Создать список невалидных ингредиентов.")
    public List<String> getInvalidIngredients() {
        return Arrays.asList("invalid123", "wrong456");
    }

    @Step
    public List<String> getEmptyIngredients() {
        return Arrays.asList();  // пустой список
    }

    @Step("Создание заказа с токеном")
    public Response placeOrder(Order order, String accessToken) {
        return new OrderClient().placeOrderWithAuth(order,accessToken);
    }

    @Step("Создание заказа с без токена")
    public Response placeOrderNoAuth(Order order) {
        return new OrderClient().placeOrderWithoutAuth(order);
    }

    @Step("Проверка успешности создания заказа")
    public void verifySuccessfulOrderPlacement(Response response) {
        assertEquals(SC_OK, response.statusCode());
        assertEquals(true, response.path("success"));
    }

    @Step("Проверка ошибки при создании заказа без ингредиентов")
    public void verifyOrderPlacementWithoutIngredients(Response response) {
        assertEquals(SC_BAD_REQUEST, response.statusCode());
        assertEquals(false, response.path("success"));
        assertEquals("Ingredient ids must be provided", response.path("message"));
    }

    @Step("Проверка ошибки при создании заказа с неверным хешем")
    public void verifyOrderPlacementWithInvalidHash(Response response) {
        assertEquals(SC_INTERNAL_SERVER_ERROR, response.statusCode());  //по документации
    }

    @Step("Создать список только с булкой")
    public List<String> getOnlyBun() {
        return Arrays.asList("61c0c5a71d1f82001bdaaa6d");  // ID булки
    }

    @Step("Создать список с булкой и соусом")
    public List<String> getBunAndSauce() {
        return Arrays.asList(
                "61c0c5a71d1f82001bdaaa6d",  // булка
                "61c0c5a71d1f82001bdaaa72"   // соус
        );
    }

}
