package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.SemesterWeek;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SemesterWeekRepository extends JpaRepository<SemesterWeek, Integer> {
    Optional<List<SemesterWeek>> findBySchoolYearID(Integer schoolYearID);

    Optional<SemesterWeek> findBySchoolYearIDAndStartDateLessThanEqualAndEndDateGreaterThanEqual(Integer schoolYearID,LocalDate date, LocalDate date1);
}
