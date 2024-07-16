package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Match_;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface MatchRepository extends JpaRepository<Match_, Integer> {

    Optional<List<Match_>> findByTournamentID(Integer tournamentID);

    void deleteAllByTournamentID(Integer tournamentID);

    Optional<List<Match_>> findAllByTournamentID(Integer tournament);

    Optional<List<Match_>> findByTournamentIDAndTeam1IDAndTeam2ID(Integer tournament, Integer team1ID, Integer team2ID);

    Optional<Match_> findByTournamentIDAndNextMatchIndex(Integer tournament, String nextMatchIndex);

    Optional<List<Match_>> findByTournamentIDAndStatus(Integer tournament, String type);

    @Query("SELECT m FROM Match_ m WHERE (m.type = :type) AND (m.tournament.ID = :tournamentID) ORDER BY m.date ASC, m.time ASC")
    Optional<List<Match_>> findByTournmentIDAndType(@Param("tournamentID")Integer tournamentID,@Param("type")String type);

    Optional<List<Match_>> findByTournamentIDAndTypeAndStatus(Integer tournamentID, String type, String status);

    @Query("SELECT m FROM Match_ m WHERE m.tournament.ID = :tournamentID AND m.type = :type AND m.status = :status ORDER BY m.time ASC")
    Optional<List<Match_>> findByTournamentIDAndTypeAndStatusSorted(@Param("tournamentID") Integer tournamentID,
                                                          @Param("type") String type,
                                                          @Param("status") String status);

    @Query("SELECT m FROM Match_ m WHERE m.date = :date AND m.time = :time AND m.tournament.ID = :tournamentId")
    Optional<List<Match_>> findByDateAndTime(@Param("tournamentId")Integer tournamentID,@Param("date") LocalDate date,@Param("time") LocalTime time);

    @Query("SELECT m FROM Match_ m WHERE m.tournament.ID = :tournamentId AND m.date = :date AND m.time BETWEEN :startTime AND :endTime")
    Optional<List<Match_>> findMatchesBetweenTimeForTournament(@Param("tournamentId") Integer tournamentId, @Param("date") LocalDate date, @Param("startTime") LocalTime startTime, @Param("endTime") LocalTime endTime);

    @Query("SELECT m FROM Match_ m WHERE m.tournament.id = :tournamentID AND m.type = :type AND m.status = :status" +
            " AND (:date IS NULL OR m.date = :date) AND (:time IS NULL OR m.time = :time)" +
            " ORDER BY m.date ASC, m.time ASC")
    Optional<List<Match_>> findByTournamentIDAndTypeAndStatusAndOptionalDateAndTime(@Param("tournamentID") Integer tournamentID,
                                                                          @Param("type") String type,
                                                                          @Param("status") String status,
                                                                          @Param("date") LocalDate date,
                                                                          @Param("time") LocalTime time);

    @Query("SELECT m FROM Match_ m WHERE m.tournament.ID = :tournamentID " +
            "AND (m.team1.ID = :teamID OR m.team2.ID = :teamID) " +
            "AND m.status = :status " +
            "ORDER BY m.date ASC, m.time ASC")
    Optional<List<Match_>> findNearestMatchByTournamentAndTeam(
            @Param("tournamentID") Integer tournamentID,
            @Param("teamID") Integer teamID,
            @Param("status") String status,
            Pageable pageable);

    @Query("SELECT m FROM Match_ m WHERE m.type = :type " +
            "AND (m.team1.ID = :teamID OR m.team2.ID = :teamID) " +
            "AND m.tournament.ID = :tournamentID " +
            "ORDER BY m.date ASC, m.time ASC")
    Optional<List<Match_>> findByTournamentIDAndTypeAndTeamID(
            @Param("tournamentID") Integer tournamentID,
            @Param("type") String type,
            @Param("teamID") Integer teamID);

    Optional<Match_> findByTournamentIDAndIndexMatch(Integer tournamentID, String matchIndex);

    @Query("SELECT m FROM Match_ m WHERE m.indexMatch LIKE CONCAT('%',:indexMatch,'%') " +
            "AND m.tournament.ID = :tournamentID ")
    Optional<List<Match_>> findByTournamentIDAndIndexMatchForPlayOff(@Param("tournamentID")Integer tournamentID,@Param("indexMatch") String indexMatch);

    @Query(value = "SELECT m FROM Match_ m WHERE m.tournament.id = :tournamentID AND m.type LIKE CONCAT('%',:type,'%')")
    Optional<List<Match_>> findByTournamentIDAndTypeLike(@Param("tournamentID") Integer tournamentID, @Param("type") String type);

    @Query("SELECT m FROM Match_ m WHERE m.tournament.id = :tournamentID AND m.type LIKE CONCAT('%',:type,'%') AND m.status = :status" +
            " AND (:date IS NULL OR m.date = :date) AND (:time IS NULL OR m.time = :time)" +
            " ORDER BY m.date ASC, m.time ASC")
    Optional<List<Match_>> findByTournamentIDAndTypeAndStatusAndOptionalDateAndTimeForGS(@Param("tournamentID") Integer tournamentID,
                                                                                         @Param("type") String type,
                                                                                         @Param("status") String status,
                                                                                         @Param("date") LocalDate date,
                                                                                         @Param("time") LocalTime time);


//    Optional<Object> findByTournamentIDAndTypeAndStatusAndDateAndTime(Integer tournamentID, String type, String status, LocalDate date, LocalTime time);
}
