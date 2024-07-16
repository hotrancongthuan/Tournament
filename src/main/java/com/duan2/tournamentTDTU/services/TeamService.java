package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.*;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import com.duan2.tournamentTDTU.repositorys.SubjectRepository;
import com.duan2.tournamentTDTU.repositorys.TeamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TeamService {

    @Autowired
    private TeamRepository teamRepository;

    public List<Team> getAllTeams(Integer tournamentID) {
        return teamRepository.findByTournamentID(tournamentID).orElse(null);
    }

    public Team getTeamById(Integer teamId) {
        return teamRepository.findById(teamId).orElse(null);
    }

    public List<Team> getTeamsByTeamIDAndStatus(Integer tournamentID, String teamID,String status){
        return teamRepository.findAllByTeam1AndTeam2AndStatus(tournamentID,teamID,status);
    }

    public List<Team> getTeamsByStatus(Integer tournamentID, String status){
        return teamRepository.findAllByTournamentIDAndStatus(tournamentID,status).orElse(null);
    }

    public Team getTeamsByGroupStage(Integer tournamentID,String groupStage){
        return teamRepository.findAllByTournamentIDAndGroupStage(tournamentID,groupStage).orElse(null);
    }

    public Team getTeamByClass1OrClass2(Integer tournamentID, String class1,String class2) {
        return teamRepository.findByTournamentIDAndClass1OrClass2(tournamentID,class1,class2).orElse(null);
    }

    public Team createTeam(Team team) {
        return teamRepository.save(team);
    }

    public List<Team> createAllTeam(List<Team> teams) {
        return teamRepository.saveAll(teams);
    }

    public void deleteTeam(Team team) {
        teamRepository.delete(team);
    }
}