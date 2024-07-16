package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.PlanYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface PlanYearRepository extends JpaRepository<PlanYear, Integer> {
    Optional<List<PlanYear>> findBySchoolYearID(Integer schoolYearId);

    Optional<List<PlanYear>> findAllBySchoolYearID(Integer schoolYearID);

    Optional<PlanYear> findBySchoolYearIDAndWeek(Integer schoolYearId, String week);

    Optional<PlanYear> findBySchoolYearIDAndDate(Integer schoolYearId, LocalDate date);
}
