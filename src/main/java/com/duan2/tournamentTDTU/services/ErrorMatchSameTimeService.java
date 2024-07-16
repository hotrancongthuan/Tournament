package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.ErrorMatchSameTime;
import com.duan2.tournamentTDTU.repositorys.ErrorMatchSameTimeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ErrorMatchSameTimeService {

    @Autowired
    private ErrorMatchSameTimeRepository errorMatchSameTimeRepository;

    public ErrorMatchSameTime saveErrorMatchSameTime(ErrorMatchSameTime errorMatchSameTime) {
        return errorMatchSameTimeRepository.save(errorMatchSameTime);
    }

    public ErrorMatchSameTime getErrorMatchSameTimeById(Integer id) {
        return errorMatchSameTimeRepository.findById(id).orElse(null);
    }

    public List<ErrorMatchSameTime> getAllErrorMatchSameTimes() {
        return errorMatchSameTimeRepository.findAll();
    }

    @Transactional
    public void deleteErrorMatchSameTime(Integer id) {
        errorMatchSameTimeRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByTournamentID(Integer tournamentID) {
        errorMatchSameTimeRepository.deleteByTournamentID(tournamentID);
    }

    public void deleteByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle) {
        errorMatchSameTimeRepository.deleteByTournamentIDAndRoundTitle(tournamentID, roundTitle);
    }

    public List<ErrorMatchSameTime> createAllByList(List<ErrorMatchSameTime> errorMatchSameTimes) {
        return errorMatchSameTimeRepository.saveAll(errorMatchSameTimes);
    }

    public List<ErrorMatchSameTime> getByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle) {
        return errorMatchSameTimeRepository.findByTournamentIDAndRoundTitle(tournamentID, roundTitle).orElse(null);
    }
}