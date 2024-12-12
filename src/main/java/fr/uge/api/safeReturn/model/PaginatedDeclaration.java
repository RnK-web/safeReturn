package fr.uge.api.safeReturn.model;

import java.util.List;

public record PaginatedDeclaration(List<Declaration> items, int totalItems, int totalPages, int currentPage) {

}
