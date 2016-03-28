package data.services;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import data.daos.AuthorizationDao;
import data.daos.TokenDao;
import data.daos.UserDao;
import data.entities.Role;
import data.entities.Token;
import data.entities.User;

@Service
@Transactional
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private TokenDao tokenDao;

    @Autowired
    private AuthorizationDao authorizationDao;

    @Override
    public UserDetails loadUserByUsername(final String usernameOrEmailOrTokenValue) throws UsernameNotFoundException {
        User user = userDao.findByTokenValue(usernameOrEmailOrTokenValue);
        if (user != null) {
            List<Role> roleList = authorizationDao.findRoleByUser(user);
            boolean isValidToken = checkTokenIsValid(usernameOrEmailOrTokenValue);
            return this.userBuilder(user.getUsername(), new BCryptPasswordEncoder().encode(""), roleList, isValidToken);
        } else {
            user = userDao.findByUsernameOrEmail(usernameOrEmailOrTokenValue);
            if (user != null) {
                return this.userBuilder(user.getUsername(), user.getPassword(), Arrays.asList(Role.AUTHENTICATED), true);
            } else {
                throw new UsernameNotFoundException("Usuario no encontrado");
            }
        }
    }

    private boolean checkTokenIsValid(final String usernameOrEmailOrTokenValue) {
        Token token = tokenDao.findByValue(usernameOrEmailOrTokenValue);
        boolean isValidToken = false;
        if (token != null) {
            isValidToken = (token.hasExpired() == false);
        }
        return isValidToken;
    }

    private org.springframework.security.core.userdetails.User userBuilder(String username, String password, List<Role> roles,
            boolean isTokenValid) {
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = isTokenValid;
        boolean accountNonLocked = true;
        List<GrantedAuthority> authorities = new ArrayList<>();
        for (Role role : roles) {
            authorities.add(new SimpleGrantedAuthority(role.roleName()));
        }
        return new org.springframework.security.core.userdetails.User(username, password, enabled, accountNonExpired, credentialsNonExpired,
                accountNonLocked, authorities);
    }
}
