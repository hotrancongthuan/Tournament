package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Tournament;
import com.duan2.tournamentTDTU.models.TournamentTakeIn;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TournamentTakeInRepository extends JpaRepository<TournamentTakeIn, String> {
    Optional<List<TournamentTakeIn>> findByTournamentID(Integer tournamentID);

    @Modifying
    @Transactional
    @Query("DELETE FROM TournamentTakeIn e WHERE e.takeIn.ID = :tournamentID AND e.tournament.ID = :takeInID")
    void deleteByTournamentIDAndTakeInID(@Param("tournamentID") Integer tournamentID,@Param("takeInID") String takeInID);


    @Modifying
    @Transactional
    @Query("DELETE FROM TournamentTakeIn t WHERE t.tournament.id = :tournamentId")
    void deleteByTournamentId(@Param("tournamentId") Integer tournamentId);

//    Optional<TournamentTakeIn> findByTournamentIDAndTakeInID(Integer tournamentID, String takeInID);

//    Optional<TournamentTakeIn> findByTournamentID(Integer tournamentID);
//
//    Optional<TournamentTakeIn> findByTakeInID(String takeInID);
}
