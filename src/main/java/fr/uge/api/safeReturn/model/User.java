package fr.uge.api.safeReturn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public record User (Long id,String username,String email,String phone) {
}
