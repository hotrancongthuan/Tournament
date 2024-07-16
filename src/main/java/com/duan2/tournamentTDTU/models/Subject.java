package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "subject")
public class Subject {

    @Id
    private String ID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String semester;

    @Column(nullable = false)
    private Integer period;

    @Column(nullable = false)
    private Integer lesson;

    @Column(nullable = false)
    private Integer credit;

    @Column(nullable = false)
    private String type;

    @Column
    private String required;

    @ManyToOne
    @JoinColumn(name = "schoolYearID", referencedColumnName = "ID",nullable = false)
    private SchoolYear schoolYear;

    // getters and setters
}