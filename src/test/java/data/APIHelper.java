package data;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class APIHelper {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();

    public static void authorization(DataHelper.AuthInfo authInfo) {
        given()
                .spec(requestSpec)
                .body(authInfo)
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);
    }

    public static String verification(DataHelper.VerificationCode verificationCode) {
        return given()
                .spec(requestSpec)
                .body(verificationCode)
                .when()
                .post("/api/auth/verification")
                .then()
                .statusCode(200)
                .extract()
                .path("token");
    }

    public static DataHelper.Card[] getCards(String token) {
        requestSpec.header("Authorization", "Bearer " + token);
        return given()
                .spec(requestSpec)
                .when()
                .get("api/cards")
                .then()
                .statusCode(200)
                .extract()
                .response().getBody().as(DataHelper.Card[].class);
    }

    public static void transferMoney(DataHelper.Transfer transfer, String token) {
        requestSpec.header("Authorization", "Bearer " + token);
        given()
                .spec(requestSpec)
                .body(transfer)
                .when()
                .post("api/transfer")
                .then()
                .statusCode(200);
    }
}
