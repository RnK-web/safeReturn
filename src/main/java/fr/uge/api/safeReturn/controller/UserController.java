package fr.uge.api.safeReturn.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import fr.uge.api.safeReturn.model.User;

@RestController
public class UserController {

	@PostMapping("/v1/users")
	public User createUser(@RequestBody User user) {
		return null;
	}
	
	@GetMapping("/v1/users")
	public List<User> getUsers() {
		return null;
	}
	
	@GetMapping("/v1/users/{id}")
	public User getUserById(@PathVariable long id) {
		return null;
	}
	
	@DeleteMapping("/v1/users/{id}")
	public void deleteUserById(@PathVariable long id) {
	}
	
	@PatchMapping("/v1/users/{id}")
	public User updateUser(@PathVariable long id) {
		return null;
	}
	
	@PostMapping("/v1/users/login")
	public void login() { 
		
	}
	
}
