package fr.uge.api.safeReturn.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
public record User (Long id,String username,String password,String email,String phone) {

    public User(Long id,String username, String password) {
        this(id, username, password, null, null);
    }
}
