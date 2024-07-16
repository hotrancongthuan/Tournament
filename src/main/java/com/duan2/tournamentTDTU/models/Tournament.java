package com.duan2.tournamentTDTU.models;

import com.duan2.tournamentTDTU.config.OnCreate;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "tournament")
public class Tournament {

    @Id
    @Column(name="ID")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ID;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "schoolYearID", referencedColumnName = "ID",nullable = false)
//    @JsonIgnore
    @Valid
    private SchoolYear schoolYear;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    @NotNull(message = "Chưa có hạn đăng ký tham gia")
    @Future(message = "Hạn đăng ký sai", groups = OnCreate.class)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate registerEndDate;

    private Integer timeHalf;
    private Integer timeBreak;
    private Integer matchSameTime;
    private Integer breakTimeTeam;

    @Column
    @NotNull(message = "Chưa nhập thành viên tối thiểu")
    @Min(value = 7, message = "ít nhất phải 7 thành viên")
    private Integer minPlayerPerTeam;

    @Column
    @NotNull(message = "Chưa nhập thành viên tối đa")
    @Max(value = 12, message = "Nhiều nhất phải 12 thành viên")
    private Integer maxPlayerPerTeam;


    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<TournamentTakeIn> tournamentTakeIns;

    @OneToMany(mappedBy = "tournament", cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<Team> teams;
}