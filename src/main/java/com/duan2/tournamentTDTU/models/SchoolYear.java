package com.duan2.tournamentTDTU.models;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "school_year")
public class SchoolYear {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @Column(nullable = false)
    @NotNull(message = "Nhập năm học của hk1")
    @Min(value = 2017, message = "HK1 không đúng")
    private Integer year1;

    @Column(nullable = false)
    @NotNull(message = "Nhập năm học của hk2")
    @Max(value = 2030, message = "HK2 không đúng")
    private Integer year2;

    @Column(nullable = false)
    private String current;

//    @OneToOne(mappedBy = "schoolYear")
//    @JsonIgnore
//    private Tournament tournament;

//    @OneToMany(mappedBy = "schoolYear", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private Set<PlanYear> planYears;
//
//    @OneToMany(mappedBy = "schoolYear", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private Set<SemesterWeek> semesterWeeks;
//
//    @OneToMany(mappedBy = "schoolYear", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
//    @JsonIgnore
//    private Set<ScheduleSubject> scheduleSubjects;

    public SchoolYear(Integer year1, Integer year2, String current) {
        this.year1 = year1;
        this.year2 = year2;
        this.current = current;
    }
}
