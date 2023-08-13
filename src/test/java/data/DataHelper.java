package data;

import lombok.Value;

public class DataHelper {
    private DataHelper() {}

    @Value
    public static class AuthInfo {
        private String login;
        private String password;
    }

    public static AuthInfo getAuthInfo() {
        return new AuthInfo("vasya", "qwerty123");
    }

    @Value
    public static class VerificationCode {
        private String login;
        private String code;
    }

    public static VerificationCode getVerificationCode(AuthInfo authInfo, String code) {
        return new VerificationCode(authInfo.getLogin(), code);
    }

    @Value
    public static class Transfer {
        private String from;
        private String to;
        private int amount;
    }

    public static Transfer getTransfer(String from, String to, int amount) {
        return new Transfer(from, to, amount);
    }
}
