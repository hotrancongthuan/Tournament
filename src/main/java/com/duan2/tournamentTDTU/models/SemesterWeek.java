package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "semesterweek")
public class SemesterWeek {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private String numWeek;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "schoolYearID", referencedColumnName = "ID",nullable = false)
    private SchoolYear schoolYear;

    public SemesterWeek(String semester, String numWeek, LocalDate startDate, LocalDate endDate, SchoolYear schoolYear) {
        this.semester = semester;
        this.numWeek = numWeek;
        this.startDate = startDate;
        this.endDate = endDate;
        this.schoolYear = schoolYear;
    }
}