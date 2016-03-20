package data.entities;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

import java.util.Calendar;
import org.junit.Before;
import org.junit.Test;

import data.entities.Token;
import data.entities.User;

public class TokenTest {

    private User user;

    private Token token;

    @Before
    public void initialize() {
        user = new User("u", "u@gmail.com", "p", Calendar.getInstance());
        token = new Token(user);
    }

    @Test
    public void testTokenUser() {
        assertTrue(token.getValue().length() > 20);
    }

    @Test
    public void testTokenExpired() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(1970, 1, 1);
        token.setCreatedDatetime(calendar);
        assertTrue(token.hasExpired());
    }

    @Test
    public void testTokenNotExpired() {
        Calendar calendar = Calendar.getInstance();
        token.setCreatedDatetime(calendar);
        assertFalse(token.hasExpired());
    }
}
