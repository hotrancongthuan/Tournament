package com.duan2.tournamentTDTU.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Year;
import java.util.Set;

@Data
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "takein")
public class TakeIn {
    @Id
    @Column(name="ID")
    private String ID;

    @Column(nullable = false)
    private Integer year;

    @OneToMany(mappedBy = "takeIn", fetch = FetchType.LAZY,  cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<TournamentTakeIn> tournamentTakeIns;

    @OneToMany(mappedBy = "takeIn", fetch = FetchType.LAZY,  cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Student> students;

    public TakeIn(String ID) {
        this.ID = ID;
    }

    public TakeIn(String ID, Integer year) {
        this.ID = ID;
        this.year = year;
    }
}
