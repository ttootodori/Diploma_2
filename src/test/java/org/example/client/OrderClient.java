package org.example.client;

import org.example.RestApi;
import org.example.model.Order;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

public class OrderClient {

    //создание заказа с авторизацией
    public Response placeOrderWithAuth(Order order, String accessToken) {
        return given()
                .contentType(ContentType.JSON)
                .header("Authorization", accessToken)
                .body(order)
                .post(RestApi.ORDERS_PATH);
    }

    //создание заказа без авторизации
    public Response placeOrderWithoutAuth(Order order) {
        return given()
                .contentType(ContentType.JSON)
                .body(order)
                .post(RestApi.ORDERS_PATH);
    }
}