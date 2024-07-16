package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Livescore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivescoreRepository extends JpaRepository<Livescore, Integer> {
    void deleteByMatchIDAndTeamID(Integer matchID, Integer teamID);

    Optional<List<Livescore>> findByMatchIDAndTeamIDOrderByMinutesAsc(Integer matchID, Integer teamID);
}
