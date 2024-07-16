package com.duan2.tournamentTDTU.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedule_subject")
public class ScheduleSubject {

    @Id
    private String ID;

    @Column(nullable = false)
    private String subjectID;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private Integer shift;

    @Column(nullable = false)
    private Integer weekday;

    @Column(nullable = false)
    private String week;

    @Column(nullable = false)
    private String semester;

    @ManyToOne
    @JoinColumn(name = "schoolYearID", referencedColumnName = "ID",nullable = false)
    private SchoolYear schoolYear;

    @OneToMany(mappedBy = "scheduleSubject", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ScheduleStudent> scheduleStudents;

    public ScheduleSubject(String ID, String subjectID, String name, Integer shift, Integer weekday, String week, String semester) {
        this.ID = ID;
        this.subjectID = subjectID;
        this.name = name;
        this.shift = shift;
        this.weekday = weekday;
        this.week = week;
        this.semester = semester;
    }

}
