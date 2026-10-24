package ru.mirea.project.user.repository;

import ru.mirea.project.user.model.User;

import java.util.List;

public interface UserRepository {

    User save(User user);

    User findById(Long id);

    User findByPhone(String phone);

    User findByEmail(String email);

    List<User> findAll();

    List<User> searchByName(String name);

    User update(User user);

    void deleteById(Long id);
}