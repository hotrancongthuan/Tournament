package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface TeamRepository  extends JpaRepository<Team, Integer> {
    @Query("SELECT t FROM Team t WHERE " +
            "(t.tournament.ID  = :tournamentID) AND " +
            "(t.class1 = :class1 OR t.class2 = :class2)")
    Optional<Team> findByTournamentIDAndClass1OrClass2(@Param("tournamentID")Integer tournamentID,@Param("class1")String class1,@Param("class2") String class2);

    @Query("SELECT t FROM Team t WHERE " +
            "(t.tournament.ID  = :tournamentID) AND " +
            "(:teamID IS NULL OR :teamID = '' OR t.class1 = :teamID OR t.class2 = :teamID) AND " +
            "(:status IS NULL OR :status = '' OR t.status = :status) ")
    List<Team> findAllByTeam1AndTeam2AndStatus(@Param("tournamentID")Integer tournamentID,@Param("teamID")String teamID,@Param("status") String status);

    Optional<List<Team>> findAllByTournamentIDAndStatus(Integer tournamentID,String status);

    Optional<Team> findAllByTournamentIDAndGroupStage(Integer tournamentID,String groupStage);

    Optional<List<Team>> findByTournamentID(Integer tournamentID);
}
