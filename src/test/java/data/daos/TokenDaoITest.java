package data.daos;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import config.PersistenceConfig;
import config.TestsPersistenceConfig;
import data.entities.Token;
import data.entities.User;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {PersistenceConfig.class, TestsPersistenceConfig.class})
public class TokenDaoITest {

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private DaosService daosService;

    @Test
    public void testFindByUser() {
        Token token = (Token) daosService.getMap().get("tu1");
        List<Token> tokens = tokenDao.findByUser(token.getUser());
        assertTrue(tokens.contains(token));

        User user = (User) daosService.getMap().get("u4");
        assertEquals(0, tokenDao.findByUser(user).size());
    }

    @Test
    public void testDeleteOldTokens() {
        List<Token> tokens = tokenDao.findAll();
        int numberOfTokensBeforeCleaning = tokens.size();
        int numberOfExpiredTokens = daosService.getNumberOfExpiredTokens(tokens);

        tokenDao.deleteOldTokens();

        int numberOfTokensAfterCleaning = tokenDao.findAll().size();

        assertEquals(numberOfTokensAfterCleaning, numberOfTokensBeforeCleaning - numberOfExpiredTokens);
    }

}
