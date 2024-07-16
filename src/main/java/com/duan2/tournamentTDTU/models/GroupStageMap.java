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
public class GroupStageMap {
    private String roundTitle;
    private String groupTitle;
    private List<GroupStageInfoMap> groupStageInfoMapList;
    private List<Match_> matchsInGroup;
}
