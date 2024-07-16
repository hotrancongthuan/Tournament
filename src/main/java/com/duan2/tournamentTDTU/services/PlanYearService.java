package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.models.Match_;
import com.duan2.tournamentTDTU.models.PlanYear;
import com.duan2.tournamentTDTU.models.Player;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import com.duan2.tournamentTDTU.repositorys.PlanYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class PlanYearService {

    @Autowired
    private PlanYearRepository planyearRepository;

    public List<PlanYear> getAllPlanYears(Integer schoolYearID) {
        return planyearRepository.findAllBySchoolYearID(schoolYearID).orElse(null);
    }

    public PlanYear getPlanYearById(Integer planYearId) {
        return planyearRepository.findById(planYearId).orElse(null);
    }

    public List<PlanYear> getPlanYearBySchoolYear(Integer SchoolYearId) {
        return planyearRepository.findBySchoolYearID(SchoolYearId).orElse(null);
    }

    public PlanYear getPlanYearByWeek(Integer SchoolYearId, String week) {
        return planyearRepository.findBySchoolYearIDAndWeek(SchoolYearId,week).orElse(null);
    }

    public PlanYear getPlanYearByDate(Integer SchoolYearId, LocalDate date) {
        return planyearRepository.findBySchoolYearIDAndDate(SchoolYearId,date).orElse(null);
    }

    public PlanYear createPlanYear(PlanYear planYear) {
        return planyearRepository.save(planYear);
    }

    public void deleteAllPlanYear(List<PlanYear> planYears){
        planyearRepository.deleteAll(planYears);
    }
}