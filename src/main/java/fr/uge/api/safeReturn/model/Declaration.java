package fr.uge.api.safeReturn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public record Declaration(long id,
	
	 String type,
	 AnimalDetails animalDetails,
	 Location location,
	 String photo,
	 int reward,
	 String status
	) {}
