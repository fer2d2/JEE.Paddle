package business.services;

import java.util.Calendar;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import data.daos.TokenDao;
import data.daos.UserDao;
import data.entities.Token;
import data.entities.User;

@Service
public class BasicDataService {
    
    /**
     * UserDao and TokenDao must be injected to create an invalid token
     */
    @Autowired
    private UserDao userDao;
    
    @Autowired
    private TokenDao tokenDao;
    
    public String generateExpiredToken(String username) {
        User user = userDao.findByUsernameOrEmail(username);
        Token invalidToken = new Token(user);
        
        Calendar createdDatetime = Calendar.getInstance();
        createdDatetime.set(1970, 1, 1, 15, 0, 0);
        invalidToken.setCreatedDatetime(createdDatetime);
        tokenDao.save(invalidToken);
        
        return invalidToken.getValue();
    }
}
