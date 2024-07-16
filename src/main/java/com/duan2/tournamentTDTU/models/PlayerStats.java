package com.duan2.tournamentTDTU.models;

import lombok.*;


@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStats {
    private String avatar;
    private String teamTitle;
    private Integer teamID;
    private Integer playerID;
    private String studentID;
    private String name;
    private Integer number;
}
