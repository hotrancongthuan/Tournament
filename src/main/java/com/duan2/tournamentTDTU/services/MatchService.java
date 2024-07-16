package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.models.Livescore;
import com.duan2.tournamentTDTU.models.Match_;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import com.duan2.tournamentTDTU.repositorys.MatchRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Service
public class MatchService {

    @Autowired
    private MatchRepository matchRepository;

    public List<Match_> getAllMatches(Integer tournament) {
        return matchRepository.findAllByTournamentID(tournament).orElse(null);
    }

    public Match_ getMatchById(Integer matchId) {
        return matchRepository.findById(matchId).orElse(null);
    }

    public List<Match_> getMatchByTeam1AndTeam2(Integer tournament,Integer team1ID, Integer team2ID) {
        return matchRepository.findByTournamentIDAndTeam1IDAndTeam2ID(tournament,team1ID,team2ID).orElse(null);
    }

    public Match_ getMatchByIndexMatch(Integer tournamentID,String matchIndex) {
        return matchRepository.findByTournamentIDAndIndexMatch(tournamentID,matchIndex).orElse(null);
    }

    public List<Match_> getMatchByIndexMatchForPlayOff(Integer tournamentID,String matchIndex) {
        return matchRepository.findByTournamentIDAndIndexMatchForPlayOff(tournamentID,matchIndex).orElse(null);
    }

    public Match_ getMatchByNextMatch(Integer tournamentID,String nextMatchIndex) {
        return matchRepository.findByTournamentIDAndNextMatchIndex(tournamentID,nextMatchIndex).orElse(null);
    }

    public List<Match_> getMatchByType(Integer tournamentID,String type) {
        return matchRepository.findByTournmentIDAndType(tournamentID,type).orElse(null);
    }

    public List<Match_> getMatchByTypeForGA(Integer tournamentID,String type) {
        return matchRepository.findByTournamentIDAndTypeLike(tournamentID,type).orElse(null);
    }

    public List<Match_> getMatchByTypeAndStatus(Integer tournamentID,String type,String status) {
        return matchRepository.findByTournamentIDAndTypeAndStatusSorted(tournamentID,type,status).orElse(null);
    }


    public List<Match_> getMatchToAttendance(Integer tournamentID, Integer teamID, String status) {
        Pageable pageable = PageRequest.of(0, 1);
        return matchRepository.findNearestMatchByTournamentAndTeam(tournamentID, teamID, status, pageable).orElse(null);
    }

    public List<Match_> getMatchByStatus(Integer tournamentID,String status) {
        return matchRepository.findByTournamentIDAndStatus(tournamentID,status).orElse(null);
    }

    public List<Match_> getMatchByTournamentID(Integer tournamentID) {
        return matchRepository.findByTournamentID(tournamentID).orElse(null);
    }

    public List<Match_> getMatchByTypeAndTeamID(Integer TournamentID,String type, Integer teamID){
        return matchRepository.findByTournamentIDAndTypeAndTeamID(TournamentID,type,teamID).orElse(null);
    }

    public List<Match_> getMatchByDateAndTime(Integer tournamentID,LocalDate date, LocalTime time) {
        return matchRepository.findByDateAndTime(tournamentID,date,time).orElse(null);
    }

    public List<Match_> getMatchesByTypeAndStatusAndDateAndTime(Integer tournamentID, String type, String status, LocalDate date, LocalTime time) {
        return matchRepository.findByTournamentIDAndTypeAndStatusAndOptionalDateAndTime(tournamentID, type, status, date, time).orElse(null);
    }

    public List<Match_> getMatchesByTypeAndStatusAndDateAndTimeForGroupStage(Integer tournamentID, String type, String status, LocalDate date, LocalTime time) {
        return matchRepository.findByTournamentIDAndTypeAndStatusAndOptionalDateAndTimeForGS(tournamentID, type, status, date, time).orElse(null);
    }

    public List<Match_> getMatchByDateAndBetweenTime(Integer tournamentID,LocalDate date, LocalTime timeStart, LocalTime timeEnd) {
        return matchRepository.findMatchesBetweenTimeForTournament(tournamentID,date,timeStart,timeEnd).orElse(null);
    }

    public Match_ createMatch(Match_ match) {
        return matchRepository.save(match);
    }

    public List<Match_> createAllMatch(List<Match_> matchs) {
        return matchRepository.saveAll(matchs);
    }

    @Transactional
    public void deleteAllByTournament(Integer tournamentID) {
        matchRepository.deleteAllByTournamentID(tournamentID);
    }

}