package com.duan2.tournamentTDTU.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoundMap {
    private String roundTitle;
    private List<Match_> matchsInRound;
}
