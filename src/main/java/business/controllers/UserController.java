package business.controllers;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import business.wrapper.SimpleUserWrapper;
import business.wrapper.UserWrapper;
import business.wrapper.UserWrapperBuilder;
import data.daos.AuthorizationDao;
import data.daos.UserDao;
import data.entities.Authorization;
import data.entities.Role;
import data.entities.User;

@Controller
public class UserController {

    private UserDao userDao;

    private AuthorizationDao authorizationDao;

    @Autowired
    public void setUserDao(UserDao userDao) {
        this.userDao = userDao;
    }

    @Autowired
    public void setAuthorizationDao(AuthorizationDao authorizationDao) {
        this.authorizationDao = authorizationDao;
    }

    public boolean registration(UserWrapper userWrapper) {
        if (null == userDao.findByUsernameOrEmail(userWrapper.getUsername())
                && null == userDao.findByUsernameOrEmail(userWrapper.getEmail())) {
            User user = new User(userWrapper.getUsername(), userWrapper.getEmail(), userWrapper.getPassword(), userWrapper.getBirthDate());
            userDao.saveAndFlush(user);
            authorizationDao.save(new Authorization(user, Role.PLAYER));
            return true;
        } else {
            return false;
        }
    }

    public boolean registrationTrainer(UserWrapper userWrapper) {
        if (null == userDao.findByUsernameOrEmail(userWrapper.getUsername())
                && null == userDao.findByUsernameOrEmail(userWrapper.getEmail())) {
            User user = new User(userWrapper.getUsername(), userWrapper.getEmail(), userWrapper.getPassword(), userWrapper.getBirthDate());
            userDao.saveAndFlush(user);
            authorizationDao.save(new Authorization(user, Role.TRAINER));
            return true;
        } else {
            return false;
        }
    }

    public List<SimpleUserWrapper> showTrainees() {
        List<User> users = authorizationDao.findUserByRole(Role.PLAYER);
        List<SimpleUserWrapper> userWrappers = new ArrayList<>();

        for (User user : users) {
            userWrappers.add(new SimpleUserWrapper(user.getId(), user.getEmail()));
        }

        return userWrappers;
    }

    public List<UserWrapper> showUsers() {
        List<User> users = userDao.findAll();
        List<UserWrapper> userWrappers = new ArrayList<>();

        for (User user : users) {
            userWrappers.add(new UserWrapperBuilder().username(user.getUsername()).email(user.getEmail()).password(user.getPassword())
                    .birthDate(user.getBirthDate()).build());
        }

        return userWrappers;
    }
}
