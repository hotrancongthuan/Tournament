package com.duan2.tournamentTDTU.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "team")
public class Team {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "tournamentID", referencedColumnName = "ID",nullable = false)
    private Tournament tournament;

    @Column(nullable = false)
    private String class1;

    private String class2;

    @Column(nullable = false)
    private Integer totalPlayer;

    @Column(nullable = false)
    private String status;

    private String groupStage;

    @OneToMany(mappedBy = "team", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Player> players;

    private Integer stop;
}
