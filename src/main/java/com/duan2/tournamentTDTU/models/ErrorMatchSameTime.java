package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "error_match_same_time")
public class ErrorMatchSameTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "tournamentID", referencedColumnName = "ID", nullable = false)
    private Tournament tournament;

    private String roundTitle;

    private LocalDate date;
    private LocalTime time;
    private Integer numberMatch;

}