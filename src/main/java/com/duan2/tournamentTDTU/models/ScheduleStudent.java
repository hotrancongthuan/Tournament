package com.duan2.tournamentTDTU.models;


import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "schedule_student")
public class ScheduleStudent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "scheduleSubjectID", referencedColumnName = "ID",nullable = false)
    private ScheduleSubject scheduleSubject;

    @ManyToOne
    @JoinColumn(name = "studentID", referencedColumnName = "ID",nullable = false)
    private Student student;

    public ScheduleStudent(ScheduleSubject scheduleSubject, Student student) {
        this.scheduleSubject = scheduleSubject;
        this.student = student;
    }
}
