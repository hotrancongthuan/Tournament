package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.models.Player;
import com.duan2.tournamentTDTU.models.Student;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import com.duan2.tournamentTDTU.repositorys.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PlayerService {

    @Autowired
    private PlayerRepository playerRepository;

    public List<Player> getAllPlayers() {
        return playerRepository.findAll();
    }

    public Player getPlayerByID(Integer playerId) {
        return playerRepository.findById(playerId).orElse(null);
    }

    public Player getPlayerByStudentIDAndTeamID(String playerId,Integer teamID) {
        return playerRepository.findByStudentIDAndTeamID(playerId,teamID).orElse(null);
    }

    public Player getPlayerByStudentIDAndTournamentID(String playerId,Integer tournamentID) {

        return playerRepository.findByStudentIDAndTournamentID(playerId,tournamentID).orElse(null);
    }


    public List<Player> getPlayerByTeam(Integer teamID) {
        return playerRepository.findByTeamIDOrderByPlayerNumber(teamID).orElse(null);
    }

    public Player getCaptain(Integer teamID, Integer captain) {
        return playerRepository.findByTeamIDAndCaptain(teamID,captain).orElse(null);
    }

    public List<Player> getPlayersByTeamIDAndStatus(Integer teamID, String status) {
        return playerRepository.findByTeamIDAndStatus(teamID,status).orElse(null);
    }

    public List<Player> getPlayersByScoreDesc(Integer tournamentId) {
        Pageable pageable = PageRequest.of(0, 11);

        return playerRepository.findByTournamentIDAndGoalGreaterThanOrderByGoalDesc(tournamentId,0,pageable).getContent();
    }

    public List<Player> getPlayersByAssistsDesc(Integer tournamentId) {
        Pageable pageable = PageRequest.of(0, 11);
        return playerRepository.findByTournamentIDAndAssistsGreaterThanOrderByAssistsDesc(tournamentId,0,pageable).getContent();
    }

    public List<Player> getPlayersByRedCardDesc(Integer tournamentId) {
        Pageable pageable = PageRequest.of(0, 11);
        return playerRepository.findByTournamentIDAndRedCardGreaterThanOrderByRedCardDesc(tournamentId,0,pageable).getContent();
    }

    public List<Player> getPlayersByYellowCardDesc(Integer tournamentId) {
        Pageable pageable = PageRequest.of(0, 11);
        return playerRepository.findByTournamentIDAndYellowCardGreaterThanOrderByYellowCardDesc(tournamentId,0,pageable).getContent();
    }

    ///////////////

    public List<Player> getPlayersByTeamIDAndScoreDesc(Integer tournamentId,Integer teamID) {
        Pageable pageable = PageRequest.of(0, 11);
        return playerRepository.findByTournamentIDAndTeamIDAndGoalGreaterThanOrderByGoalDesc(tournamentId,teamID,0,pageable).getContent();
    }

    public List<Player> getPlayersByTeamIDAndAssistsDesc(Integer tournamentId,Integer teamID) {
        Pageable pageable = PageRequest.of(0, 11);
        return playerRepository.findByTournamentIDAndTeamIDAndAssistsGreaterThanOrderByAssistsDesc(tournamentId,teamID,0,pageable).getContent();
    }

    public List<Player> getPlayersTeamIDAndByRedCardDesc(Integer tournamentId,Integer teamID) {
        Pageable pageable = PageRequest.of(0, 11);
        return playerRepository.findByTournamentIDAndTeamIDAndRedCardGreaterThanOrderByRedCardDesc(tournamentId,teamID,0,pageable).getContent();
    }

    public List<Player> getPlayersByTeamIDAndYellowCardDesc(Integer tournamentId,Integer teamID) {
        Pageable pageable = PageRequest.of(0, 11);
        return playerRepository.findByTournamentIDAndTeamIDAndYellowCardGreaterThanOrderByYellowCardDesc(tournamentId,teamID,0,pageable).getContent();
    }


    public Player createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public List<Player> createAllPlayer(List<Player> players) {
        return playerRepository.saveAll(players);
    }

    public void deletePlayers(List<Player> players) {
        playerRepository.deleteAll(players);
    }


}