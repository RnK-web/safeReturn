package fr.uge.api.safeReturn.controller;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.uge.api.safeReturn.model.PaginatedItems;
import fr.uge.api.safeReturn.model.Payment;
import fr.uge.api.safeReturn.model.Token;
import fr.uge.api.safeReturn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import fr.uge.api.safeReturn.model.User;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class UserController {
	
	record UserInfos(Long id, String username, String email, String phone) {}

	private static final int PAGE_SIZE = 5;
	@Autowired
	private UserService userService;

	@GetMapping("/v1/users")
	public PaginatedItems < UserInfos > getUsers(@RequestParam(name = "page", required = false) String page, @RequestHeader("Authorization") String authorizationHeader) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}

		if (page == null) {
			var users = userService.getAllUsers().stream().map(u -> new UserInfos(u.id(), u.username(), u.email(), u.phone())).toList();
			return new PaginatedItems < UserInfos > (users, users.size(), 0, 0);
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
			var payments = userService.getAllUsers().stream().map(u -> new UserInfos(u.id(), u.username(), u.email(), u.phone())).toList().subList(pageNum * PAGE_SIZE, toIndex);
			return new PaginatedItems < UserInfos > (payments, totalItems, totalPages, currentPage);
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
	@ResponseStatus(value = HttpStatus.CREATED)
	public UserInfos registerUser(
			@RequestBody Map < String, String > payload) {

		String username = payload.get("username");
		String password = payload.get("password");
		String email = payload.get("email");
		String phone = payload.get("phone");

		Map < String, Object > response = new LinkedHashMap < > ();

		var registered = userService.registerUser(username, password, email, phone);
		if (registered == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid data");
		}
		return new UserInfos(registered.id(), registered.username(), registered.email(), registered.phone());
	}

	@PostMapping("/v1/users/login")
	public Token loginUser(
			@RequestBody Map < String, String > payload) {

		String username = payload.get("username");
		String password = payload.get("password");

		String token = userService.loginUser(username, password);

		if (token == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "invalid username or password");

		}
		userService.addToken(token);
		return new Token(token);
	}

	@PatchMapping("/v1/users/{id}")
	public ResponseEntity < Map < String, Object >> updateUser(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader, @RequestBody Map < String, String > payload) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}
		Map < String, Object > response = new LinkedHashMap < > ();
		var previousUser = userService.getUserById(id);
		if (previousUser == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id {" + id + "} not found !");
		}

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
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteUserById(@PathVariable long id, @RequestHeader("Authorization") String authorizationHeader) {

		if (!userService.isValidToken(authorizationHeader)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "invalid token");
		}
		var resp = userService.deleteUserById(id);
		if (resp == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User with id {" + id + "} not found !");
		}
	}

}