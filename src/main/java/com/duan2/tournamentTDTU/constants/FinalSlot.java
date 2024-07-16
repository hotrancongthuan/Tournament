package com.duan2.tournamentTDTU.constants;

public enum FinalSlot {
    G1(32,8,2,0),
    G2(28,7,2,2),
    G3(24,8,2,0);

    private final Integer totalTeam;
    private final Integer numberGroup;
    private final Integer teamTop;
    private final Integer teamBestChoice;

    FinalSlot(Integer totalTeam, Integer numberGroup, Integer teamTop, Integer teamBestChoice) {
        this.totalTeam = totalTeam;
        this.numberGroup = numberGroup;
        this.teamTop = teamTop;
        this.teamBestChoice = teamBestChoice;
    }

    public Integer getTotalTeam() {
        return totalTeam;
    }

    public Integer getNumberGroup() {
        return numberGroup;
    }

    public Integer getTeamTop() {
        return teamTop;
    }

    public Integer getTeamBestChoice() {
        return teamBestChoice;
    }
}
