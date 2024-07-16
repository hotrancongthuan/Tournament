package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "group_stage_info")
public class GroupStageInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(name = "group_title")
    private String groupTitle;

    @ManyToOne
    @JoinColumn(name = "tournamentID", referencedColumnName = "ID")
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "teamID", referencedColumnName = "ID")
    private Team team;
    private Integer totalMatch;
    private Integer win;
    private Integer lose;
    private Integer draw;
    @Column(name = "goals_scored")
    private Integer goalsScored;
    @Column(name = "goals_against")
    private Integer goalsAgainst;
    @Column(name = "goals_difference")
    private Integer goalsDifference;
    private Integer point;
    @Column(name = "last_match")
    private String lastMatch;
    @Column(name = "best_third")
    private Integer bestThird;
    private Integer rank;
    private Integer redCard;
    private Integer yellowCard;
}
