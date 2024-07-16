package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "match_")
public class Match_ {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "tournamentID",referencedColumnName = "ID", nullable = false)
    private Tournament tournament;

    @Column(nullable = false)
    private String indexMatch;

    private String nextMatchIndex;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime time;

    @Column(nullable = false)
    private String type;

    @ManyToOne
    @JoinColumn(name = "team1ID",referencedColumnName = "ID")
    private Team team1;

    @Column(name = "team1_status")
    private String team1Status;
    private Integer scoreTeam1;
    private Integer penaltyScoreTeam1;
    private Integer yellowCardTeam1;
    private Integer redCardTeam1;

    @ManyToOne
    @JoinColumn(name = "team2ID",referencedColumnName = "ID")
    private Team team2;

    @Column(name = "team2_status")
    private String team2Status;
    private Integer scoreTeam2;
    private Integer penaltyScoreTeam2;
    private Integer yellowCardTeam2;
    private Integer redCardTeam2;

    @Column(nullable = false)
    private String status;

    private String reasonTeam1;
    private String reasonTeam2;
    private Integer penaltyCheck;
}