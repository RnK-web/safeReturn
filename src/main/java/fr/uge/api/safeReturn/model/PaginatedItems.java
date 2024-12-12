package fr.uge.api.safeReturn.model;

import java.util.List;

public record PaginatedItems<T>(List<T> items, int totalItems, int totalPages, int currentPage) {

}
