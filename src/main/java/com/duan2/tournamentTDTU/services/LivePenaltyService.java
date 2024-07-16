package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.LiveCard;
import com.duan2.tournamentTDTU.models.LivePenalty;
import com.duan2.tournamentTDTU.models.Livescore;
import com.duan2.tournamentTDTU.repositorys.LivePenaltyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class LivePenaltyService {
    @Autowired
    private LivePenaltyRepository livePenaltyRepository;


    public LivePenaltyService(LivePenaltyRepository livePenaltyRepository) {
        this.livePenaltyRepository = livePenaltyRepository;
    }

    public List<LivePenalty> findAll() {
        return livePenaltyRepository.findAll();
    }

    public LivePenalty findById(Integer id) {
        return livePenaltyRepository.findById(id).orElse(null);
    }

    public LivePenalty save(LivePenalty livePenalty) {
        return livePenaltyRepository.save(livePenalty);
    }

    public List<LivePenalty> getLivePenaltyByMatchIDAndTeamID(Integer matchID,Integer teamID) {
        return livePenaltyRepository.findByMatchIDAndTeamID(matchID,teamID).orElse(null);
    }

    public List<LivePenalty> createAll(List<LivePenalty> livePenalties) {
        return livePenaltyRepository.saveAll(livePenalties);
    }

    @Transactional
    public void deleteByMatchIDAndTeamID(Integer matchID, Integer teamID){
        livePenaltyRepository.deleteByMatchIDAndTeamID(matchID,teamID);
    }
    @Transactional
    public void deleteById(Integer id) {
        livePenaltyRepository.deleteById(id);
    }
}
