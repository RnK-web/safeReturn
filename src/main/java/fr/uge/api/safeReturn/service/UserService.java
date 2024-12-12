package fr.uge.api.safeReturn.service;

import fr.uge.api.safeReturn.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class UserService {
    //private List<User> users = new ArrayList<>();
    private final HashMap<Long, User> users = new HashMap<>();
    private Long nextId = 0L;

    @Autowired
    private JwtService jwtService;

    // Constructeur pour ajouter des utilisateurs initiaux
    public UserService() {
        initializeUsers();
    }

    private void initializeUsers() {
        registerUser("user1", "password1", "user1@example.com", "1234567890");
        registerUser("user2", "password2", "user2@example.com", "1234567891");
        registerUser("user3", "password3", "user3@example.com", "1234567892");
        registerUser("user4", "password4", "user4@example.com", "1234567893");
        registerUser("user5", "password5", "user5@example.com", "1234567894");
        registerUser("user6", "password6", "user6@example.com", "1234567895");
        registerUser("user7", "password7", "user7@example.com", "1234567896");
        registerUser("user8", "password8", "user8@example.com", "1234567897");
        registerUser("user9", "password9", "user9@example.com", "1234567898");
        registerUser("user10", "password10", "user10@example.com", "1234567899");
    }

    public User registerUser(String username, String password, String email, String phone) {
        if (!users.isEmpty()) {
            if (users.values().stream().anyMatch(u -> u.username().equals(username))) {
                return null;
            }
        }


        User newUser = new User(nextId++, username, password, email, phone);
        users.put(newUser.id(),newUser);
        return newUser;
    }


    public String loginUser(String username, String password) {
        User user = users.values().stream()
                .filter(u -> u.username().equals(username) && u.password().equals(password))
                .findFirst()
                .orElse(null);


        if (user != null) {
            return jwtService.generateToken(user);
        }
        return null;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users.values());
    }

    public User getUserById(Long id) {

        return users.get(id);
    }

    public User deleteUserById(Long id) {
        return users.remove(id);
    }

    public User replace(Long id, User user) {
        return users.replace(id, user);
    }
}