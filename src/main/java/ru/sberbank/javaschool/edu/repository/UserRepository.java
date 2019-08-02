package ru.sberbank.javaschool.edu.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.sberbank.javaschool.edu.domain.User;

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByLogin(String login);
    User findUserByActcode(String code);

    User findUserById(Long id);

    @Modifying(clearAutomatically = true)
    @Query("UPDATE User user "
            + "SET user.name = :newName, user.surname = :newSurname "
            + "WHERE user.login=:login")
    void updateUser(
            @Param("login") String login,
            @Param("newName") String newName,
            @Param("newSurname") String newSurname
    );

}