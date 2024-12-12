package fr.uge.api.safeReturn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public record Payment (
	 long id,
	 long rewarderId,
	 long receiverId,
	 long correlationId,
	
	 float amount,
	 String currency,
	 String method,
	 String status
) {
}
