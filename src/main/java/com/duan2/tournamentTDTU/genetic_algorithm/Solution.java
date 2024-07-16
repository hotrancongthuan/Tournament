package com.duan2.tournamentTDTU.genetic_algorithm;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Data
@Getter
@Setter
@AllArgsConstructor
public class Solution {
    private List<ADN> adns;
    private double point;
    private double error;

    public Solution() {
        this.adns = new ArrayList<>();
        this.point = 0;
        this.error = 0;
    }

    public Solution(List<ADN> adns) {
        this.adns = adns;
        this.point = 0;
        this.error = 0;
    }
}
