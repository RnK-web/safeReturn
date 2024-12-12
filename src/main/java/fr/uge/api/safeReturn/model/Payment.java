package fr.uge.api.safeReturn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public record Payment (
	 Long id,
	 Long rewarderId,
	 Long receiverId,
	 Long correlationId,
	
	 Float amount,
	 String currency,
	 String method,
	 String status
) {
}
