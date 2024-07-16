package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "planyear")
public class PlanYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String week;

    @Column(nullable = false)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @Column(nullable = false)
    private Integer numDate;

    @ManyToOne
    @JoinColumn(name = "schoolYearID", referencedColumnName = "ID",nullable = false)
    private SchoolYear schoolYear;

    public PlanYear(String name, String week, LocalDate date, Integer numDate, SchoolYear schoolYear) {
        this.name = name;
        this.week = week;
        this.date = date;
        this.numDate = numDate;
        this.schoolYear = schoolYear;
    }
}
