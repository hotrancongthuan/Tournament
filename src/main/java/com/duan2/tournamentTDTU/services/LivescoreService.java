package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.LivePenalty;
import com.duan2.tournamentTDTU.models.Livescore;
import com.duan2.tournamentTDTU.repositorys.LivescoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LivescoreService {

    @Autowired
    private LivescoreRepository livescoreRepository;

    public List<Livescore> getAllLivescores() {
        return livescoreRepository.findAll();
    }

    public Livescore getLivescoreById(Integer livescoreId) {
        return livescoreRepository.findById(livescoreId).orElse(null);
    }

    public List<Livescore> getLivescoreByMatchIDAndTeamID(Integer matchID,Integer teamID) {
        return livescoreRepository.findByMatchIDAndTeamIDOrderByMinutesAsc(matchID,teamID).orElse(null);
    }

    public List<Livescore> createAll(List<Livescore> livescores) {
        return livescoreRepository.saveAll(livescores);
    }

    @Transactional
    public void deleteByMatchIDAndTeamID(Integer matchID, Integer teamID){
        livescoreRepository.deleteByMatchIDAndTeamID(matchID,teamID);
    }
    @Transactional
    public Livescore createLivescore(Livescore livescore) {
        return livescoreRepository.save(livescore);
    }

}