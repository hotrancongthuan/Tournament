package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tournamenttakein")
public class TournamentTakeIn{
    @Id
    @Column(name="ID")
    private String ID;

    @ManyToOne
    @JoinColumn(name = "tournamentID",referencedColumnName = "ID", nullable = false)
    private Tournament tournament;

    @ManyToOne
    @JoinColumn(name = "takeInID",referencedColumnName = "ID", nullable = false)
    private TakeIn takeIn;
}
