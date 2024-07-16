package com.duan2.tournamentTDTU.updateMatch;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Data
@Getter
@Setter
public class DataModel {
    private Integer matchID;
    private List<ScoreData> scoreTeam1;
    private List<ScoreData> scoreTeam2;
    private List<CardData> cardTeam1;
    private List<CardData> cardTeam2;
    private List<PenaltyData> penaltyTeam1;
    private List<PenaltyData> penaltyTeam2;
}
