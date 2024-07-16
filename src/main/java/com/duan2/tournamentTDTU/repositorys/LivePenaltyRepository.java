package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.LivePenalty;
import com.duan2.tournamentTDTU.models.Livescore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LivePenaltyRepository extends JpaRepository<LivePenalty, Integer> {
    void deleteByMatchIDAndTeamID(Integer matchID, Integer teamID);

    Optional<List<LivePenalty>> findByMatchIDAndTeamID(Integer matchID, Integer teamID);
}