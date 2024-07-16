package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.LiveCard;
import com.duan2.tournamentTDTU.models.Livescore;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface LiveCardRepository extends JpaRepository<LiveCard, Integer> {
    void deleteByMatchIDAndTeamID(Integer matchID, Integer teamID);

    Optional<List<LiveCard>> findByMatchIDAndTeamIDOrderByMinutesAsc(Integer matchID, Integer teamID);
}