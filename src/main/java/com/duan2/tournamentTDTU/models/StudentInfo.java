package com.duan2.tournamentTDTU.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.Set;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StudentInfo {
    private String ID;

    private String name;

    private TakeIn takeIn;

    private String classID;

    private String mode;

    @Override
    public String toString() {
        return "StudentInfo{" +
                "ID='" + ID + '\'' +
                ", name='" + name + '\'' +
                ", classID='" + classID + '\'' +
                ", mode='" + mode + '\'' +
                '}';
    }
}
