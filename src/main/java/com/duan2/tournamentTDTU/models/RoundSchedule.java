package com.duan2.tournamentTDTU.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "roundschedule")
public class RoundSchedule {
    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer scheduleID;

    @ManyToOne
    @JoinColumn(name = "tournamentID", referencedColumnName = "ID",nullable = false)
    private Tournament tournament;

    @Column(name="status")
    private String scheduleStatus;

    private String roundTitle;

    private Integer numberMatch;

    private Integer numberMatchDone;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateStart;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateEnd;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime timeMorningStart;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime timeMorningEnd;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime timeEveningStart;

    @DateTimeFormat(pattern = "HH:mm")
    private LocalTime timeEveningEnd;

    private Integer error;
}
