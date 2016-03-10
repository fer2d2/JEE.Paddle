package data.daos;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import data.entities.Token;

@Repository
public class TokenDaoImpl implements TokenExtended {

    @Autowired
    private TokenDao tokenDao;

    @Override
    public void deleteOldTokens() {
        List<Token> tokens = tokenDao.findAll();

        for (Token token : tokens) {
            if (token.hasExpired()) {
                tokenDao.delete(token);
            }
        }
    }

}
