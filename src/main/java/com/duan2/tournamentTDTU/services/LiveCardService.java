package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.LiveCard;
import com.duan2.tournamentTDTU.models.Livescore;
import com.duan2.tournamentTDTU.repositorys.LiveCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class LiveCardService {
    @Autowired
    private LiveCardRepository liveCardRepository;

    public List<LiveCard> findAll() {
        return liveCardRepository.findAll();
    }

    public LiveCard findById(Integer id) {
        return liveCardRepository.findById(id).orElse(null);
    }

    public LiveCard save(LiveCard liveCard) {
        return liveCardRepository.save(liveCard);
    }

    public List<LiveCard> createAll(List<LiveCard> liveCards) {
        return liveCardRepository.saveAll(liveCards);
    }

    public List<LiveCard> getLiveCardByMatchIDAndTeamID(Integer matchID, Integer teamID) {
        return liveCardRepository.findByMatchIDAndTeamIDOrderByMinutesAsc(matchID,teamID).orElse(null);
    }


    @Transactional
    public void deleteByMatchIDAndTeamID(Integer matchID, Integer teamID){
        liveCardRepository.deleteByMatchIDAndTeamID(matchID,teamID);
    }

    @Transactional
    public void deleteById(Integer id) {
        liveCardRepository.deleteById(id);
    }
}
