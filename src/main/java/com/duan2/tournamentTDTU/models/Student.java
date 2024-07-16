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
@Table(name = "student")
public class Student {
    @Id
    private String ID;

    @ManyToOne
    @JoinColumn(name = "take_inID",referencedColumnName = "ID", nullable = false)
    private TakeIn takeIn;

    @Column(name="classID",nullable = false)
    private String classID;

    @Column(nullable = false)
    private String mode;

    @OneToMany(mappedBy = "student", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<ScheduleStudent> scheduleStudents;

    public Student(String ID, TakeIn takeIn, String classID, String mode) {
        this.ID = ID;
        this.takeIn = takeIn;
        this.classID = classID;
        this.mode = mode;
    }

}
