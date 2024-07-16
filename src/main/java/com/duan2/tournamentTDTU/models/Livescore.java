package com.duan2.tournamentTDTU.models;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "livescore")
public class Livescore {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchID", referencedColumnName = "ID", nullable = false)
    private Match_ match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_score", referencedColumnName = "ID", nullable = false)
    private Player playerScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_assist", referencedColumnName = "ID")
    private Player playerAssist;

    @Column(nullable = false)
    private Integer minutes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamID", nullable = false)
    private Team team;

    @Column(nullable = false)
    private String type;

}
