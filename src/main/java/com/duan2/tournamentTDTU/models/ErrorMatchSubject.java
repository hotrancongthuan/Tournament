package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "error_match_subject")
public class ErrorMatchSubject {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "tournamentID", referencedColumnName = "ID", nullable = false)
    private Tournament tournament;

    private String roundTitle;

    @ManyToOne
    @JoinColumn(name = "matchID", referencedColumnName = "ID", nullable = false)
    private Match_ match;

    @ManyToOne
    @JoinColumn(name = "schedule_studentID", referencedColumnName = "ID", nullable = false)
    private ScheduleStudent scheduleStudent;
}
