package fr.uge.api.safeReturn.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.uge.api.safeReturn.model.Payment;
import fr.uge.api.safeReturn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import fr.uge.api.safeReturn.model.User;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {

	@Autowired
	private UserService userService;


	@GetMapping("/v1/users")
	public List<User> getUsers() {
		return userService.getAllUsers();
	}
	
	@GetMapping("/v1/users/{id}")
	public User getUserById(@PathVariable long id) {
		return userService.getUserById(id);
	}
	


	@PostMapping("/v1/users/register")
	public ResponseEntity<Map<String, Object>> registerUser(
			@RequestBody Map<String, String> payload) {

		String username = payload.get("username");
		String password = payload.get("password");

		Map<String, Object> response = new HashMap<>();

		boolean registered = userService.registerUser(username, password);

		if (registered) {
			response.put("success", true);
			response.put("message", "Utilisateur enregistré avec succès");
			return ResponseEntity.ok(response);
		} else {
			response.put("success", false);
			response.put("message", "Nom d'utilisateur déjà existant");
			return ResponseEntity.badRequest().body(response);
		}
	}


	@PostMapping("/v1/users/login")
	public ResponseEntity<Map<String, Object>> loginUser(
			@RequestBody Map<String, String> payload) {

		String username = payload.get("username");
		String password = payload.get("password");

		Map<String, Object> response = new HashMap<>();

		String token = userService.loginUser(username, password);

		if (token != null) {
			response.put("token", token);
			return ResponseEntity.ok(response);
		} else {
			response.put("message", "Identifiants invalides");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}
	}


	@PatchMapping("/v1/users/{id}")
	public User updateUser(@PathVariable long id, @RequestBody Map<String, String> payload) {

		var previousUser = userService.getUserById(id);

		String username = payload.get("username");
		String password = payload.get("password");
		String email = payload.get("email") == null ? previousUser.email() : payload.get("email");
		String phone = payload.get("phone") == null ? previousUser.phone() : payload.get("phone");


		if (previousUser == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id {" + id + "} not found !");
		}
		var patchedUser = new User(id, username, password, email, phone);
		userService.replace(id, patchedUser);
		return patchedUser;
	}

	@DeleteMapping("/v1/users/{id}")
	public ResponseEntity<Map<String, Object>> deleteUserById(@PathVariable long id) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Utilisateur supprimé avec succès");
		userService.deleteUserById(id);
		return ResponseEntity.ok(response);
	}



}
