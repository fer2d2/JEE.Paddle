package data.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import data.daos.AuthorizationDao;
import data.daos.CourtDao;
import data.daos.ReserveDao;
import data.daos.TokenDao;
import data.daos.UserDao;

@Service
public class DataService {

    @Autowired
    private Populate populate;

    @Autowired
    private ReserveDao reserveDao;

    @Autowired
    private AuthorizationDao authorizationDao;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private CourtDao courtDao;

    @Autowired
    private UserDao userDao;

    public void deleteAllExceptAdmin() {
        reserveDao.deleteAll();
        authorizationDao.deleteAll();
        tokenDao.deleteAll();
        courtDao.deleteAll();
        userDao.deleteAll();
        populate.createDefaultAdmin();
    }

}
