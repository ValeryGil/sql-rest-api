package data;

import lombok.Data;
import lombok.Value;

public class DataHelper {
    private DataHelper() {}

    @Value
    public static class AuthInfo {
        String login;
        String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        String login;
        String code;
    }

    public static VerificationCode getVerificationCode(String login) {
        return new VerificationCode(login, SQLHelper.getVerificationCodeByLogin(login));
    }

    @Value
    public static class Transfer {
        String from;
        String to;
        int amount;
    }

    @Data
    public static class Card {
        String id;
        String number;
        String balance;
    }
}
