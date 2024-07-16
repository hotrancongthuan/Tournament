package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "user")
public class User {

    @Id
    private String ID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String gender;

    private String avatar;

    private String email;

    public User(String ID, String name, String gender) {
        this.ID = ID;
        this.name = name;
        this.gender = gender;
    }
}
