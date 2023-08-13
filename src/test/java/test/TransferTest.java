package test;

import data.Card;
import data.DataHelper;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.filter.log.LogDetail;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import lombok.val;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.sql.DriverManager;
import java.sql.SQLException;

import static io.restassured.RestAssured.given;

public class TransferTest {
    private static RequestSpecification requestSpec = new RequestSpecBuilder()
            .setBaseUri("http://localhost")
            .setPort(9999)
            .setAccept(ContentType.JSON)
            .setContentType(ContentType.JSON)
            .log(LogDetail.ALL)
            .build();
    int firstCardBalance;
    int secondCardBalance;
    int amount = 5_000;

    @Test
    public void shouldTransfer() throws SQLException {
        given()
                .spec(requestSpec)
                .body(DataHelper.getAuthInfo())
                .when()
                .post("/api/auth")
                .then()
                .statusCode(200);

        val codeSQL = "SELECT code FROM auth_codes WHERE created = (SELECT max(created) FROM auth_codes);";
        val runner = new QueryRunner();

        try (val connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/app", "app", "pass")) {
            val code = runner.query(connection, codeSQL, new ScalarHandler<String>());
            System.out.println(code);

            String token =
                    given()
                            .spec(requestSpec)
                            .body(DataHelper.getVerificationCode(DataHelper.getAuthInfo(), code))
                            .when()
                            .post("/api/auth/verification")
                            .then()
                            .statusCode(200)
                            .extract()
                            .path("token");
            System.out.println(token);

            Card[] cardsOne =
                    given()
                            .spec(requestSpec)
                            .header("Authorization", "Bearer" + token)
                            .when()
                            .get("/api/cards")
                            .then()
                            .statusCode(200)
                            .extract()
                            .as(Card[].class);
            System.out.println(cardsOne[0].getBalance());
            System.out.println(cardsOne[1].getBalance());
            firstCardBalance = Integer.parseInt(cardsOne[0].getBalance());
            secondCardBalance = Integer.parseInt(cardsOne[1].getBalance());

            given()
                    .spec(requestSpec)
                    .header("Authorization", "Bearer" + token)
                    .body(DataHelper.getTransfer("5559 0000 0000 0001", "5559 0000 0000 0002", amount))
                    .when()
                    .post("/api/transfer")
                    .then()
                    .statusCode(200);

            Card[] cardsTwo =
                    given()
                            .spec(requestSpec)
                            .header("Authorization", "Bearer" + token)
                            .when()
                            .get("/api/cards")
                            .then()
                            .statusCode(200)
                            .extract()
                            .as(Card[].class);
            System.out.println(cardsTwo[0].getBalance());
            System.out.println(cardsTwo[1].getBalance());
            firstCardBalance = Integer.parseInt(cardsTwo[0].getBalance());
            secondCardBalance = Integer.parseInt(cardsTwo[1].getBalance());

            Assertions.assertEquals(firstCardBalance - amount, secondCardBalance);
            Assertions.assertEquals(secondCardBalance + amount, firstCardBalance);
        }
    }
}
