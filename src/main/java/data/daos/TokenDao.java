package data.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import data.entities.Token;
import data.entities.User;

public interface TokenDao extends JpaRepository<Token, Integer> {

    Token findByUser(User user);

    List<Token> findAll();

}
