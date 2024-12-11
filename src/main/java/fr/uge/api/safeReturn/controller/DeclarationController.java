package fr.uge.api.safeReturn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import fr.uge.api.safeReturn.model.Declaration;

@RestController
public class DeclarationController {
	private Long nextDeclarationId = 0l;
	private final HashMap<Long, Declaration> declarationStore = new HashMap<>();

	@PostMapping("/v1/declarations")
	public Declaration createDeclaration(@RequestBody Declaration declaration) {
		var decla = new Declaration(nextDeclarationId, declaration.type(), declaration.animalDetails(), declaration.location(), declaration.photo(), declaration.reward(), declaration.status());
		declarationStore.put(nextDeclarationId, decla);
		nextDeclarationId++;
		return decla;
	}
	
	@GetMapping("/v1/declarations")
	public List<Declaration> getDeclarations() {
		if (declarationStore.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No declarations were found !");
		}
		return declarationStore.values().stream().toList();
	}
	
	@GetMapping("/v1/declarations/{id}")
	public Declaration getDeclarationById(@PathVariable long id) {
		var decla = declarationStore.get(id);
		if (decla == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Declaration with id {" + id + "} not found !");
		}
		return decla;
	}

	@DeleteMapping("/v1/declarations/{id}")
	public void deleteDeclarationById(@PathVariable long id) {
		var removedDecla = declarationStore.remove(id);
		if (removedDecla == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Declaration with id {" + id + "} not found !");
		}
	}

	@PatchMapping("/v1/declarations/{id}")
	public Declaration updateDeclaration(@PathVariable long id, @RequestBody Declaration declaration) {
		var patchedDecla = new Declaration(id, declaration.type(), declaration.animalDetails(), declaration.location(), declaration.photo(), declaration.reward(), declaration.status());
		var previousDecla = declarationStore.replace(id, patchedDecla);
		if (previousDecla == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Declaration with id {" + id + "} not found !");
		}
		return patchedDecla;
	}
	
}
