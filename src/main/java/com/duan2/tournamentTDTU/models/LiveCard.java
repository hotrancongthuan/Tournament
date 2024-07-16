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
@Table(name = "livecard")
public class LiveCard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "matchID", referencedColumnName = "ID", nullable = false)
    private Match_ match;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teamID", referencedColumnName = "ID", nullable = false)
    private Team team;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "playerID", referencedColumnName = "ID", nullable = false)
    private Player player;

    private Integer minutes;
    private String type;
}
