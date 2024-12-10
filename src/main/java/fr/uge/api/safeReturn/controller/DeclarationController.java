package fr.uge.api.safeReturn.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import fr.uge.api.safeReturn.model.Declaration;
import fr.uge.api.safeReturn.model.User;

@RestController
public class DeclarationController {

	@PostMapping("/v1/declarations")
	public Declaration createDeclaration(@RequestBody Declaration declaration) {
		return null;
	}
	
	@GetMapping("/v1/declarations")
	public List<Declaration> getDeclarations() {
		return null;
	}
	
	@GetMapping("/v1/declarations/{id}")
	public Declaration getDeclarationById(@PathVariable long id) {
		return null;
	}

	@DeleteMapping("/v1/declarations/{id}")
	public void deleteDeclarationById(@PathVariable long id) {
	}

	@PatchMapping("/v1/declarations/{id}")
	public Declaration updateDeclaration(@PathVariable long id, @RequestBody Declaration declaration) {
		return null;
	}
	
}
