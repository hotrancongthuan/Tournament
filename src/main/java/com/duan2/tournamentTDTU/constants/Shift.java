package com.duan2.tournamentTDTU.constants;

public enum Shift {
    S1("06:50", "09:20",1),
    S2("09:30", "12:00",2),
    S3("12:45", "15:15",3),
    S4("15:25", "17:55",4),
    S5("18:05", "20:35",5);

    private final String startTime;
    private final String endTime;

    private final Integer shiftNumber;

    Shift(String startTime, String endTime, Integer shiftNumber) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.shiftNumber = shiftNumber;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }
    public Integer getShiftNumber() {
        return shiftNumber;
    }
}
