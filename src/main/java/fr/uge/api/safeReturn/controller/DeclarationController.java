package fr.uge.api.safeReturn.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import fr.uge.api.safeReturn.model.Declaration;
import fr.uge.api.safeReturn.model.PaginatedItems;
import fr.uge.api.safeReturn.model.Payment;

@RestController
public class DeclarationController {
	private static final int PAGE_SIZE = 5;
	private Long nextDeclarationId = 0l;
	private final HashMap<Long, Declaration> declarationStore = new HashMap<>();

	@PostMapping("/v1/declarations")
	@ResponseStatus(value = HttpStatus.CREATED)
	public Declaration createDeclaration(@RequestBody Declaration declaration) {
		var decla = new Declaration(nextDeclarationId, declaration.type(), declaration.animalDetails(), declaration.location(), declaration.photo(), declaration.reward(), declaration.status());
		declarationStore.put(nextDeclarationId, decla);
		nextDeclarationId++;
		return decla;
	}
	
	@GetMapping("/v1/declarations")
	public PaginatedItems<Declaration> getDeclarations(@RequestParam(name = "page", required = false) String page,
																										@RequestParam(name = "status", required = false) String status,
																										@RequestParam(name = "animal", required = false) String animal) {
		if (declarationStore.isEmpty()) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "No declarations were found !");
		}
		Predicate<Declaration> filter = (d) -> true;
		if (status != null) {
			filter = filter.and((d) -> d.type().toLowerCase().equals(status.toLowerCase()));
		}
		if (animal != null) {
			filter = filter.and((d) -> d.animalDetails().species().toLowerCase().equals(animal.toLowerCase()));
		}
		if (page == null) {
			var declas = declarationStore.values().stream().filter(filter).toList();
			return new PaginatedItems<Declaration>(declas, declas.size(), 0, 0);
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
			var items = declarationStore.values().stream().filter(filter).toList();
			var totalPages = items.size() / PAGE_SIZE;
			var totalItems = items.size();
			var currentPage = pageNum;
			var toIndex = Math.min((pageNum + 1) * PAGE_SIZE, items.size());
			var declas = items.subList(pageNum * PAGE_SIZE, toIndex);
			System.out.println(items);
			return new PaginatedItems<Declaration>(declas, totalItems, totalPages, currentPage);
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
	@ResponseStatus(value = HttpStatus.NO_CONTENT)
	public void deleteDeclarationById(@PathVariable long id) {
		var removedDecla = declarationStore.remove(id);
		if (removedDecla == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Declaration with id {" + id + "} not found !");
		}
	}

	@PatchMapping("/v1/declarations/{id}")
	public Declaration updateDeclaration(@PathVariable long id, @RequestBody Declaration updateValues) {
		var previousDeclaration = declarationStore.get(id);
		if (previousDeclaration == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Declaration with id {" + id + "} not found !");
		}
		var type = updateValues.type() == null ? previousDeclaration.type() : updateValues.type();
		var animalDetails = updateValues.animalDetails() == null ? previousDeclaration.animalDetails() : updateValues.animalDetails();
		var location = updateValues.location() == null ? previousDeclaration.location() : updateValues.location();
		var photo = updateValues.photo() == null ? previousDeclaration.photo() : updateValues.photo();
		var reward = updateValues.reward() == null ? previousDeclaration.reward() : updateValues.reward();
		var status = updateValues.status() == null ? previousDeclaration.status() : updateValues.status();
		var patchedDecla = new Declaration(id, type, animalDetails, location, photo, reward, status);
		declarationStore.replace(id, patchedDecla);
		return patchedDecla;
	}
	
}
