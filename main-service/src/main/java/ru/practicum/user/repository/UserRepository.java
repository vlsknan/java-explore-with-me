package ru.practicum.user.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.user.model.User;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, Integer> {
    @Query("select u from User u " +
            "where u.id in (:ids) ")
    List<User> findAllById(int[] ids, PageRequest page);

    Boolean existsUserByName(String name);
}
