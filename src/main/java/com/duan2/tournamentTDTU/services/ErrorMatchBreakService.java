package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.ErrorMatchBreak;
import com.duan2.tournamentTDTU.repositorys.ErrorMatchBreakRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ErrorMatchBreakService {

    @Autowired
    private ErrorMatchBreakRepository errorMatchBreakRepository;

    public ErrorMatchBreak saveErrorMatchBreak(ErrorMatchBreak errorMatchBreak) {
        return errorMatchBreakRepository.save(errorMatchBreak);
    }

    public ErrorMatchBreak getErrorMatchBreakById(Integer id) {
        return errorMatchBreakRepository.findById(id).orElse(null);
    }

    public List<ErrorMatchBreak> getAllErrorMatchBreaks() {
        return errorMatchBreakRepository.findAll();
    }

    @Transactional
    public void deleteErrorMatchBreak(Integer id) {
        errorMatchBreakRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByTournamentID(Integer tournamentID) {
        errorMatchBreakRepository.deleteByTournamentID(tournamentID);
    }

    @Transactional
    public void deleteByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle) {
        errorMatchBreakRepository.deleteByTournamentIDAndRoundTitle(tournamentID, roundTitle);
    }

    public List<ErrorMatchBreak> createAllByList(List<ErrorMatchBreak> errorMatchBreaks) {
        return errorMatchBreakRepository.saveAll(errorMatchBreaks);
    }

    public List<ErrorMatchBreak> getByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle) {
        return errorMatchBreakRepository.findByTournamentIDAndRoundTitle(tournamentID, roundTitle).orElse(null);
    }
}