package com.duan2.tournamentTDTU.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name="account")
public class Account {

    @Id
    @Column(name="ID")
    private String ID;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private Integer status;

    private String cookies;

    @Column(nullable = false)
    private String role;

    public Account(String ID, String password, Integer status, String role) {
        this.ID = ID;
        this.password = password;
        this.status = status;
        this.role = role;
    }
}
