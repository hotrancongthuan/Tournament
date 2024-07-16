package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Tournament;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TournamentRepository  extends JpaRepository<Tournament, Integer> {
    Optional<Tournament> findBySchoolYearID(Integer schoolYearId);
}
