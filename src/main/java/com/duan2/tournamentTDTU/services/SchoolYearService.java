package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.ScheduleSubject;
import com.duan2.tournamentTDTU.models.SchoolYear;
import com.duan2.tournamentTDTU.repositorys.ScheduleSubjectRepository;
import com.duan2.tournamentTDTU.repositorys.SchoolYearRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolYearService {
    @Autowired
    private SchoolYearRepository schoolYearRepository;

    public List<SchoolYear> getAllSchoolYears() {
        return schoolYearRepository.findAll();
    }

    public SchoolYear getSchoolYearById(Integer schoolYearId) {
        return schoolYearRepository.findById(schoolYearId).orElse(null);
    }

    public SchoolYear getSchoolYearByYear1AndYear2(Integer year1, Integer year2) {
        return schoolYearRepository.findByYear1AndYear2(year1,year2).orElse(null);
    }


    public SchoolYear getSchoolYearByCurrent(String current) {
        return schoolYearRepository.findByCurrent(current).orElse(null);
    }

    public SchoolYear saveSchoolYear(SchoolYear schoolYear) {
        return schoolYearRepository.save(schoolYear);
    }

    public SchoolYear createSchoolYear(SchoolYear newSchoolYear) {

        List<SchoolYear> schoolYears = schoolYearRepository.findAll();

        if(!schoolYears.isEmpty()){
            for(SchoolYear schoolYear: schoolYears){
                schoolYear.setCurrent("0");
                saveSchoolYear(schoolYear);
            }
        }

        return schoolYearRepository.save(newSchoolYear);
    }
}
