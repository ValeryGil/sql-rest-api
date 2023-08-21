package test;

import data.DataHelper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static data.APIHelper.*;
import static data.SQLHelper.*;

public class TransferTest {
    DataHelper.AuthInfo authInfo;
    DataHelper.Card[] cards;
    int indexFirstCard;
    int indexSecondCard;
    int firstCardBalance;
    int secondCardBalance;
    String token;

    @BeforeEach
    public void setUp() {
        reloadVerificationCode();
        authInfo = DataHelper.getAuthInfo();
        authorization(authInfo);
        var verifyData = DataHelper.getVerificationCode(authInfo.getLogin());
        token = verification(verifyData);
        cards = getCards(token);
        int i = 0;
        for (DataHelper.Card card : cards) {
            card.setNumber(getCardNumberById(card.getId()));
            i++;
        }
    }

    @AfterEach
    public void returnBalance() {
        reloadBalanceCards(cards[indexFirstCard].getId(), firstCardBalance);
        reloadBalanceCards(cards[indexSecondCard].getId(), secondCardBalance);
    }

    @Test
    public void shouldTransfer() {
        indexFirstCard = 0;
        indexSecondCard = 1;
        int amount = 5_000;
        firstCardBalance = Integer.parseInt(cards[indexFirstCard].getBalance());
        secondCardBalance = Integer.parseInt(cards[indexSecondCard].getBalance());

        var transfer = new DataHelper.Transfer(cards[indexFirstCard].getNumber(),
                cards[indexSecondCard].getNumber(), amount);
        transferMoney(transfer, token);

        Assertions.assertEquals(firstCardBalance - amount, getBalanceById(cards[indexFirstCard].getId()));
        Assertions.assertEquals(secondCardBalance + amount, getBalanceById(cards[indexSecondCard].getId()));
    }

    @Test
    public void shouldTransferNegativeAmount() {
        indexFirstCard = 0;
        indexSecondCard = 1;
        int amount = -1_000;
        firstCardBalance = Integer.parseInt(cards[indexFirstCard].getBalance());
        secondCardBalance = Integer.parseInt(cards[indexSecondCard].getBalance());

        var transfer = new DataHelper.Transfer(cards[indexFirstCard].getNumber(),
                cards[indexSecondCard].getNumber(), amount);
        transferMoney(transfer, token);

        Assertions.assertEquals(firstCardBalance, getBalanceById(cards[indexFirstCard].getId()));
        Assertions.assertEquals(secondCardBalance, getBalanceById(cards[indexSecondCard].getId()));
    }

    @Test
    public void shouldTransferZeroAmount() {
        indexFirstCard = 0;
        indexSecondCard = 1;
        int amount = 0;
        firstCardBalance = Integer.parseInt(cards[indexFirstCard].getBalance());
        secondCardBalance = Integer.parseInt(cards[indexSecondCard].getBalance());

        var transfer = new DataHelper.Transfer(cards[indexFirstCard].getNumber(),
                cards[indexSecondCard].getNumber(), amount);
        transferMoney(transfer, token);

        Assertions.assertEquals(firstCardBalance, getBalanceById(cards[indexFirstCard].getId()));
        Assertions.assertEquals(secondCardBalance, getBalanceById(cards[indexSecondCard].getId()));
    }

    @Test
    public void shouldTransferAmountMoreThanBalance() {
        indexFirstCard = 0;
        indexSecondCard = 1;
        int amount = 11_000;
        firstCardBalance = Integer.parseInt(cards[indexFirstCard].getBalance());
        secondCardBalance = Integer.parseInt(cards[secondCardBalance].getBalance());

        var transfer = new DataHelper.Transfer(cards[indexFirstCard].getNumber(),
                cards[indexSecondCard].getNumber(), amount);
        transferMoney(transfer, token);

        Assertions.assertEquals(firstCardBalance, getBalanceById(cards[indexFirstCard].getId()));
        Assertions.assertEquals(secondCardBalance, getBalanceById(cards[indexSecondCard].getId()));
    }
}
