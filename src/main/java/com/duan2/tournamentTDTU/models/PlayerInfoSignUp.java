package com.duan2.tournamentTDTU.models;

import jakarta.persistence.Entity;
import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerInfoSignUp {
    private String ID;
    private String name;
    private String classID;
    private Integer number;
}
