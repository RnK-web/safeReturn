package fr.uge.api.safeReturn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "PAYMENT")
@Getter
@Setter
@NoArgsConstructor
public class Payment {

	private Long rewarderId;
	private Long receiverId;
	
	@Id 
	private Long correlationId;
	
	private Float amount;
	private String currency;
	private String method;
	private String status;

}
