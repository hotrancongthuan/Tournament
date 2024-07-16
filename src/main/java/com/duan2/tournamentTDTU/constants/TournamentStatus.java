package com.duan2.tournamentTDTU.constants;

public enum TournamentStatus {
   C("CLOSE"),O("OPEN"),N("NOT"),I("INPROGRESS");

    private String status;

    private TournamentStatus(String status){
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}
