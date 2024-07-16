package com.duan2.tournamentTDTU.models;

import lombok.*;

import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RefusePlayers {

    private List<String> players;
    private String teamID;
}
