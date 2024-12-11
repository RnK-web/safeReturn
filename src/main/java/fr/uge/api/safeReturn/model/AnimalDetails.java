package fr.uge.api.safeReturn.model;

import java.util.List;

import jakarta.persistence.Entity;

public record AnimalDetails(String species, String breed, List<String> color, float weight, float size, int age) {

}
