package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.models.SemesterWeek;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import com.duan2.tournamentTDTU.repositorys.SemesterWeekRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class SemesterWeekService {

    @Autowired
    private SemesterWeekRepository semesterWeekRepository;

    public List<SemesterWeek> getAllSemesterWeeks() {
        return semesterWeekRepository.findAll();
    }

    public SemesterWeek getSemesterWeekById(Integer semesterWeekId) {
        return semesterWeekRepository.findById(semesterWeekId).orElse(null);
    }

    public List<SemesterWeek> getSemesterWeekBySchoolYear(Integer schoolYearID) {
        return semesterWeekRepository.findBySchoolYearID(schoolYearID).orElse(null);
    }

    public SemesterWeek getSemesterWeekByDate(Integer schoolYearID,LocalDate date) {
        return semesterWeekRepository.findBySchoolYearIDAndStartDateLessThanEqualAndEndDateGreaterThanEqual(schoolYearID,date, date).orElse(null);
    }

    public SemesterWeek createSemesterWeek(SemesterWeek semesterWeek) {
        return semesterWeekRepository.save(semesterWeek);
    }

    public void deleteSemesterWeek(List<SemesterWeek> semesterWeeks){
        semesterWeekRepository.deleteAll(semesterWeeks);
    }


}