package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.ErrorMatchBreak;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ErrorMatchBreakRepository extends JpaRepository<ErrorMatchBreak, Integer> {

    void deleteByTournamentID(Integer tournamentID);

    Optional<List<ErrorMatchBreak>> findByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle);

    void deleteByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle);
}