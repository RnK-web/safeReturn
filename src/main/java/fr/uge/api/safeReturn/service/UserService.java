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