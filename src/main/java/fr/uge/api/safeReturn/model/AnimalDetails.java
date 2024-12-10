package fr.uge.api.safeReturn.model;

import java.util.List;

public record AnimalDetails(String species, String breed, List<String> color, Float weight, Float size, Integer age) {

}
