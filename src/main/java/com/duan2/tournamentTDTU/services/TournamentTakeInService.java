package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.TournamentTakeIn;
import com.duan2.tournamentTDTU.repositorys.TournamentTakeInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TournamentTakeInService {
    @Autowired
    private TournamentTakeInRepository tournamentTakeInRepository;

    public List<TournamentTakeIn> getAllTournamentTakeIn() {
        return tournamentTakeInRepository.findAll();
    }

    public TournamentTakeIn getTournamentTakeInById(String takeInID) {
        return tournamentTakeInRepository.findById(takeInID).orElse(null);
    }

    public List<TournamentTakeIn> getTournamentTakeInByTournament(Integer tournamentID) {
        return tournamentTakeInRepository.findByTournamentID(tournamentID).orElse(null);
    }

//    public TournamentTakeIn getTournamentTakeInByTournamentAndTakeIn(Integer tournamentID,String takeInID) {
//        return tournamentTakeInRepository.findByTournamentIDAndTakeInID(tournamentID,takeInID).orElse(null);
//    }
//
//    public TournamentTakeIn getTournamentTakeInByTakeIn(String TakeInID) {
//        return tournamentTakeInRepository.findByTakeInID(TakeInID).orElse(null);
//    }

    public TournamentTakeIn createTournamentTakeIn(TournamentTakeIn tournamentTakeIn) {
        return tournamentTakeInRepository.save(tournamentTakeIn);
    }

    public void deleteTournamentTakeIn(TournamentTakeIn tournamentTakeIn) {
        tournamentTakeInRepository.delete(tournamentTakeIn);
    }

//    public void deleteByTournamentIDAndTakeInID(Integer tournamentID, String takeInID) {
//        //TournamentTakeIn tournamentTakeIn = getTournamentTakeInByTournamentAndTakeIn(tournamentID,takeInID);
//        tournamentTakeInRepository.deleteByTournamentIDAndTakeInID(tournamentID,takeInID);
//    }

    @Transactional
    public void deleteTournamentTakeInsByTournament(Integer tournamentId) {
        tournamentTakeInRepository.deleteByTournamentId(tournamentId);
    }

}
