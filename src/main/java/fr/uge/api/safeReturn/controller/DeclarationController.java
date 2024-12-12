package fr.uge.api.safeReturn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import fr.uge.api.safeReturn.model.Declaration;
import fr.uge.api.safeReturn.model.PaginatedDeclaration;

@RestController
public class DeclarationController {
	private static final int PAGE_SIZE = 50;
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
	public PaginatedDeclaration getDeclarations(@RequestParam(name = "page", required = false) String page) {
		if (declarationStore.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No declarations were found !");
		}
		if (page == null) {
			var declas = declarationStore.values().stream().toList();
			return new PaginatedDeclaration(declas, declas.size(), 1, 1);
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
			var totalPages = declarationStore.size() / PAGE_SIZE;
			var totalItems = declarationStore.size();
			var currentPage = pageNum;
			var toIndex = Math.min((pageNum + 1) * PAGE_SIZE, declarationStore.size());
			var declas = declarationStore.values().stream().toList().subList(pageNum * PAGE_SIZE, toIndex);
			return new PaginatedDeclaration(declas, totalItems, totalPages, currentPage);
		} catch (Exception e) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Page is not a valid number");
		}
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
