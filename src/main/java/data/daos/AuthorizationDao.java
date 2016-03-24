package data.daos;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import data.entities.Authorization;
import data.entities.Role;
import data.entities.User;

public interface AuthorizationDao extends JpaRepository<Authorization, Integer> {

    @Query("select authorization.role from Authorization authorization where authorization.user = ?1")
    List<Role> findRoleByUser(User user);
    
    @Query("select authorization.role from Authorization authorization where authorization.user = ?1 and authorization.role = ?2")
    Role findOneRoleByUser(User user, Role role);
}
