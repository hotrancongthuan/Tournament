package com.duan2.tournamentTDTU.models;

import jakarta.persistence.Entity;
import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SignUpTeam {

    private String class1;

    private String class2;

    private String captain;

    private List<Player> players;
}
