package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "attendance")
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @ManyToOne
    @JoinColumn(name = "matchID",referencedColumnName = "ID", nullable = false)
    private Match_ match;

    @ManyToOne
    @JoinColumn(name = "teamID",referencedColumnName = "ID", nullable = false)
    private Team team;

    @ManyToOne
    @JoinColumn(name = "playerID", referencedColumnName = "ID", nullable = false)
    private Player player;

    private String status;

    private String reason;
}
