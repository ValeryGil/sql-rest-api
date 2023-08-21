package data;

import lombok.SneakyThrows;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLHelper {
    private static QueryRunner runner;
    private static Connection connection;

    @SneakyThrows
    public static void setUp() {
        runner = new QueryRunner();
        connection = DriverManager.getConnection
                ("jdbc:mysql://localhost:3306/app", "app", "pass");
    }

    @SneakyThrows
    public static void setDown() {
        setUp();
        reloadVerificationCode();
        var sqlQueryOne = "DELETE FROM card_transactions;";
        var sqlQueryTwo = "DELETE FROM cards;";
        var sqlQueryThree = "DELETE FROM users;";
        runner.update(connection, sqlQueryOne);
        runner.update(connection, sqlQueryTwo);
        runner.update(connection, sqlQueryThree);
    }

    @SneakyThrows
    public static void reloadVerificationCode() {
        setUp();
        var sqlQuery = "DELETE FROM auth_codes;";
        runner.update(connection, sqlQuery);
    }

    @SneakyThrows
    public static void reloadBalanceCards(String id, int balance) {
        setUp();
        var sqlQuery = "UPDATE cards " + "SET balance_in_kopecks = ? " + "WHERE id IN (?);";
        runner.update(connection, sqlQuery, balance * 100, id);
    }

    @SneakyThrows
    public static String getVerificationCodeByLogin(String login) {
        setUp();
        var sqlQuery = "SELECT code FROM auth_codes " + "JOIN users ON user_id = users.id " +
                "WHERE login IN (?) " + "ORDER BY created DESC LIMIT 1;";
        return runner.query(connection, sqlQuery, new ScalarHandler<>(), login);
    }

    @SneakyThrows
    public static String getCardNumberById(String id) {
        setUp();
        var sqlQuery = "SELECT number FROM cards WHERE id IN (?);";
        return runner.query(connection, sqlQuery, new ScalarHandler<>(), id);
    }

    @SneakyThrows
    public static int getBalanceById(String id) {
        setUp();
        var sqlQuery = "SELECT balance_in_kopecks FROM cards WHERE id IN (?);";
        return runner.query(connection, sqlQuery, new ScalarHandler<Integer>(), id) / 100;
    }
}
