package com.duan2.tournamentTDTU.services;
import com.duan2.tournamentTDTU.models.ErrorMatchSubject;
import com.duan2.tournamentTDTU.repositorys.ErrorMatchSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
@Service
public class ErrorMatchSubjectService {

    @Autowired
    private ErrorMatchSubjectRepository errorMatchSubjectRepository;

    public ErrorMatchSubject saveErrorMatchSubject(ErrorMatchSubject errorMatchSubject) {
        return errorMatchSubjectRepository.save(errorMatchSubject);
    }

    public ErrorMatchSubject getErrorMatchSubjectById(Integer id) {
        return errorMatchSubjectRepository.findById(id).orElse(null);
    }

    public List<ErrorMatchSubject> getAllErrorMatchSubjects() {
        return errorMatchSubjectRepository.findAll();
    }

    @Transactional
    public void deleteErrorMatchSubject(Integer id) {
        errorMatchSubjectRepository.deleteById(id);
    }

    @Transactional
    public void deleteAllByTournamentID(Integer tournamentID) {
        errorMatchSubjectRepository.deleteByTournamentID(tournamentID);
    }

    @Transactional
    public void deleteByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle) {
        errorMatchSubjectRepository.deleteByTournamentIDAndRoundTitle(tournamentID, roundTitle);
    }

    public List<ErrorMatchSubject> createAllByList(List<ErrorMatchSubject> errorMatchSubjects) {
        return errorMatchSubjectRepository.saveAll(errorMatchSubjects);
    }

    public List<ErrorMatchSubject> getByTournamentIDAndRoundTitle(Integer tournamentID, String roundTitle) {
        return errorMatchSubjectRepository.findByTournamentIDAndRoundTitle(tournamentID, roundTitle).orElse(null);
    }
}