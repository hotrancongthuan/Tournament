package com.duan2.tournamentTDTU.genetic_algorithm;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ADN {
    private Integer matchID;
    private String week;
    private Integer weekday;
    private LocalTime time;
    private LocalDate date;
}
