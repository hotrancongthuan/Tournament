package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.ScheduleSubject;
import com.duan2.tournamentTDTU.models.SchoolYear;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SchoolYearRepository extends JpaRepository<SchoolYear, Integer> {
    Optional<SchoolYear> findByCurrent(String current);

    Optional<SchoolYear> findByYear1AndYear2(Integer year1, Integer year2);
}
