package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.ErrorMatchSubject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ErrorMatchSubjectRepository extends JpaRepository<ErrorMatchSubject, Integer> {

    void deleteByTournamentID(Integer tournamentID);

    Optional<List<ErrorMatchSubject>> findByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle);

    void deleteByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle);
}