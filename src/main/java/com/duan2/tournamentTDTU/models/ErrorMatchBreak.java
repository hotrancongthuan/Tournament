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
@Table(name = "error_match_break")
public class ErrorMatchBreak {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "tournamentID", referencedColumnName = "ID", nullable = false)
    private Tournament tournament;

    @Column(nullable = false)
    private String roundTitle;

    @ManyToOne
    @JoinColumn(name = "match1ID", referencedColumnName = "ID", nullable = false)
    private Match_ match1;

    @ManyToOne
    @JoinColumn(name = "match2ID", referencedColumnName = "ID", nullable = false)
    private Match_ match2;

    private Integer teamID;

    private Integer dateBetween;
    private Integer timeBetween;

}