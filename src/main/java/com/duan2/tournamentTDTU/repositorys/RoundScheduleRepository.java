package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Player;
import com.duan2.tournamentTDTU.models.RoundSchedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoundScheduleRepository extends JpaRepository<RoundSchedule, Integer> {
    Optional<List<RoundSchedule>> findByTournamentID(Integer tournamentID);

    Optional<RoundSchedule> findByTournamentIDAndRoundTitle(Integer tournamentID,String roundTitle);

    void deleteAllByTournamentID(Integer tournamentID);

    Optional<List<RoundSchedule>> findByTournamentIDAndScheduleStatusOrderByScheduleID(Integer tournamentID, String status);
}
