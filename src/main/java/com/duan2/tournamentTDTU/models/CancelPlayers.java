package com.duan2.tournamentTDTU.models;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CancelPlayers {
    private List<String> cancelPlayers;
    private List<String> aceptPlayers;
    private String teamID;
}
