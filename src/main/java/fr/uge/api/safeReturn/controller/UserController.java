package fr.uge.api.safeReturn.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.uge.api.safeReturn.model.PaginatedItems;
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

	private static final int PAGE_SIZE = 5;
	@Autowired
	private UserService userService;

	@GetMapping("/v1/users")
	public PaginatedItems < User > getUsers(@RequestParam(name = "page", required = false) String page, @RequestHeader("Authorization") String authorizationHeader) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}

		if (page == null) {
			var users = userService.getAllUsers();
			return new PaginatedItems < User > (users, users.size(), 0, 0);
		}
		int pageNum;
		try {
			pageNum = Integer.parseInt(page);
		} catch (NumberFormatException e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page must be an int");
		}
		if (pageNum < 0) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page can't be negative");
		}
		try {
			var totalItems = userService.getAllUsers().size();
			var totalPages = totalItems / PAGE_SIZE;
			var currentPage = pageNum;
			var toIndex = Math.min((pageNum + 1) * PAGE_SIZE, totalItems);
			var payments = userService.getAllUsers().stream().toList().subList(pageNum * PAGE_SIZE, toIndex);
			return new PaginatedItems < User > (payments, totalItems, totalPages, currentPage);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page is not a valid number");
		}
	}

	@GetMapping("/v1/users/{id}")
	public ResponseEntity < Map < String, Object >> getUserById(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}
		Map < String, Object > response = new LinkedHashMap < > ();
		var user = userService.getUserById(id);
		response.put("id", user.id());
		response.put("username", user.username());
		response.put("email", user.email());
		response.put("phone", user.phone());
		return ResponseEntity.ok(response);
	}

	@PostMapping("/v1/users")
	public ResponseEntity < Map < String, Object >> registerUser(
			@RequestBody Map < String, String > payload) {

		String username = payload.get("username");
		String password = payload.get("password");
		String email = payload.get("email");
		String phone = payload.get("phone");

		Map < String, Object > response = new LinkedHashMap < > ();

		var registered = userService.registerUser(username, password, email, phone);

		if (registered != null) {
			response.put("id", registered.id());
			response.put("username", registered.username());
			response.put("email", registered.email());
			response.put("phone", registered.phone());
			return ResponseEntity.ok(response);
		} else {
			return ResponseEntity.badRequest().body(response);
		}
	}

	@PostMapping("/v1/users/login")
	public ResponseEntity < Map < String, Object >> loginUser(
			@RequestBody Map < String, String > payload) {

		String username = payload.get("username");
		String password = payload.get("password");

		Map < String, Object > response = new HashMap < > ();

		String token = userService.loginUser(username, password);

		if (token == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid username or password");

		}
		response.put("token", token);
		userService.addToken(token);
		return ResponseEntity.ok(response);
	}

	@PatchMapping("/v1/users/{id}")
	public ResponseEntity < Map < String, Object >> updateUser(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader, @RequestBody Map < String, String > payload) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}
		Map < String, Object > response = new LinkedHashMap < > ();
		var previousUser = userService.getUserById(id);

		String username = payload.get("username") == null ? previousUser.username() : payload.get("username");
		String password = previousUser.password();
		String email = payload.get("email") == null ? previousUser.email() : payload.get("email");
		String phone = payload.get("phone") == null ? previousUser.phone() : payload.get("phone");

		var patchedUser = new User(id, username, password, email, phone);
		userService.replace(id, patchedUser);

		response.put("id", patchedUser.id());
		response.put("username", patchedUser.username());
		response.put("email", patchedUser.email());
		response.put("phone", patchedUser.phone());
		return ResponseEntity.ok(response);
	}

	@DeleteMapping("/v1/users/{id}")
	public void deleteUserById(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}
		userService.deleteUserById(id);
	}

}