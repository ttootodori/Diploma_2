package org.example.client;

import org.example.RestApi;
import org.example.model.User;
import io.restassured.response.Response;
import static io.restassured.RestAssured.given;
import io.restassured.http.ContentType;

public class UserClient {

    public Response create(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .post(RestApi.REGISTER_PATH);
    }

    public Response login(User user) {
        return given()
                .contentType(ContentType.JSON)
                .body(user)
                .post(RestApi.LOGIN_PATH);
    }

    public Response delete(String accessToken) {

        return given()
                .header("Authorization", accessToken)
                .delete(RestApi.USER_PATH);

    }
}