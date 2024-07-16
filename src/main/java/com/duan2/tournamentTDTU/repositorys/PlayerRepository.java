package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Player;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PlayerRepository extends JpaRepository<Player, Integer> {
    Optional<List<Player>> findByTeamIDOrderByPlayerNumber(Integer teamID);

    Optional<Player> findByTeamIDAndCaptain(Integer teamID, Integer captain);

    Optional<List<Player>> findByTeamIDAndStatus(Integer teamID, String status);

    Optional<Player> findByStudentIDAndTeamID(String playerId,Integer teamID);

    Optional<Player> findByStudentIDAndTournamentID(String playerId, Integer tournamentID);


    Page<Player> findByTournamentIDAndGoalGreaterThanOrderByGoalDesc(Integer tournamentId, int i, Pageable pageable);

    Page<Player> findByTournamentIDAndAssistsGreaterThanOrderByAssistsDesc(Integer tournamentId, int i, Pageable pageable);

    Page<Player> findByTournamentIDAndRedCardGreaterThanOrderByRedCardDesc(Integer tournamentId, int i, Pageable pageable);

    Page<Player> findByTournamentIDAndYellowCardGreaterThanOrderByYellowCardDesc(Integer tournamentId, int i, Pageable pageable);


    ///
    Page<Player> findByTournamentIDAndTeamIDAndGoalGreaterThanOrderByGoalDesc(Integer tournamentId,Integer teamID, int i, Pageable pageable);

    Page<Player> findByTournamentIDAndTeamIDAndAssistsGreaterThanOrderByAssistsDesc(Integer tournamentId,Integer teamID, int i, Pageable pageable);

    Page<Player> findByTournamentIDAndTeamIDAndRedCardGreaterThanOrderByRedCardDesc(Integer tournamentId,Integer teamID, int i, Pageable pageable);

    Page<Player> findByTournamentIDAndTeamIDAndYellowCardGreaterThanOrderByYellowCardDesc(Integer tournamentId,Integer teamID, int i, Pageable pageable);
}
