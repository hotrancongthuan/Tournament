package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.ErrorMatchSameTime;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ErrorMatchSameTimeRepository extends JpaRepository<ErrorMatchSameTime, Integer> {

    void deleteByTournamentID(Integer tournamentID);

    Optional<List<ErrorMatchSameTime>> findByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle);

    void deleteByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle);
}
