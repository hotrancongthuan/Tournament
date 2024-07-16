package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Player;
import com.duan2.tournamentTDTU.models.RoundSchedule;
import com.duan2.tournamentTDTU.models.Team;
import com.duan2.tournamentTDTU.repositorys.PlayerRepository;
import com.duan2.tournamentTDTU.repositorys.RoundScheduleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RoundScheduleService {
    @Autowired
    private RoundScheduleRepository roundScheduleRepository;

    public RoundSchedule getRoundScheduleByID(Integer roundScheduleID) {
        return roundScheduleRepository.findById(roundScheduleID).orElse(null);
    }

    public List<RoundSchedule> getAllRoundSchedule(Integer tournamentID) {
        return roundScheduleRepository.findByTournamentID(tournamentID).orElse(null);
    }
    public RoundSchedule getRoundScheduleByRoundTitle(Integer tournamentID,String roundTitle) {
        return roundScheduleRepository.findByTournamentIDAndRoundTitle(tournamentID,roundTitle).orElse(null);
    }

    public List<RoundSchedule> getRoundScheduleByStatus(Integer tournamentID,String status) {
        return roundScheduleRepository.findByTournamentIDAndScheduleStatusOrderByScheduleID(tournamentID,status).orElse(null);
    }

//    public List<RoundSchedule> getRoundScheduleByStatusDone(Integer tournamentID,String status) {
//        return roundScheduleRepository.findByTournamentIDAndScheduleStatus(tournamentID,status).orElse(null);
//    }

    public RoundSchedule createRoundSchedule(RoundSchedule roundSchedule) {
        return roundScheduleRepository.save(roundSchedule);
    }

    public List<RoundSchedule> createAllRoundSchedule(List<RoundSchedule> roundSchedules) {
        return roundScheduleRepository.saveAll(roundSchedules);
    }

    @Transactional
    public void deleteAllByTournament(Integer tournamentID) {
        roundScheduleRepository.deleteAllByTournamentID(tournamentID);
    }


}
