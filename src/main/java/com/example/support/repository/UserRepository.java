package com.example.support.repository;

import com.example.support.model.User;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public List<User> findByRole(String role) {
        return users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(Collectors.toList());
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public void deleteById(String id) {
        users.remove(id);
    }
}