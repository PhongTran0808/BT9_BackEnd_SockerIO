package com.example.support;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class UserRepository {
    private final Map<String, User> users = new ConcurrentHashMap<>();

    public UserRepository() {
        // Tạo sẵn một số user mẫu
        save(new User("customer1", "customer1", "default", "CUSTOMER"));
        save(new User("customer2", "customer2", "default", "CUSTOMER"));
        save(new User("customer3", "customer3", "default", "CUSTOMER"));
        save(new User("manager", "manager", "default", "MANAGER"));
    }

    public User save(User user) {
        users.put(user.getId(), user);
        return user;
    }

    public Optional<User> findById(String id) {
        return Optional.ofNullable(users.get(id));
    }

    public Optional<User> findByUsername(String username) {
        return users.values().stream()
                .filter(user -> username.equals(user.getUsername()))
                .findFirst();
    }

    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }

    public List<User> findByRole(String role) {
        return users.values().stream()
                .filter(user -> role.equals(user.getRole()))
                .collect(ArrayList::new, (list, user) -> list.add(user), ArrayList::addAll);
    }

    public void deleteById(String id) {
        users.remove(id);
    }
}