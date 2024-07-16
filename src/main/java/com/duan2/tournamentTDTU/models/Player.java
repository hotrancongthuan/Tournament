package com.duan2.tournamentTDTU.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "player")
public class Player {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    private String studentID;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "tournamentID", referencedColumnName = "ID",nullable = false)
    private Tournament tournament;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String classID;

    @ManyToOne
    @JoinColumn(name = "teamID",referencedColumnName = "ID", nullable = false)
    private Team team;

    @Column(nullable = false)
    private Integer captain;

    private Integer goal;
    private Integer assists;
    private Integer yellowCard;
    private Integer redCard;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private Integer playerNumber;

    private Integer height;
    private Integer weight;

    private Integer bannedNextMatch;

    private String reasonBanned;

}
