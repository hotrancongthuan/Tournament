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
public class GroupStageInfoMap {
    private Integer teamID;
    private String groupTitle;
    private String teamTitle;
    private Integer totalMatch;
    private Integer win;
    private Integer lose;
    private Integer draw;
    private Integer goalsScored;
    private Integer goalsAgainst;
    private Integer goalsDifference;
    private Integer point;
    private List<String> lastMatchs;
    private Integer rank;
}
