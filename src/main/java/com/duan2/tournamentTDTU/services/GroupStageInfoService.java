package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.GroupStageInfo;
import com.duan2.tournamentTDTU.repositorys.GroupStageInfoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupStageInfoService {

    @Autowired
    private GroupStageInfoRepository groupStageInfoRepository;

    public GroupStageInfo saveGroupStageInfo(GroupStageInfo groupStageInfo) {
        return groupStageInfoRepository.save(groupStageInfo);
    }

    public GroupStageInfo create(GroupStageInfo groupStageInfo) {
        return groupStageInfoRepository.save(groupStageInfo);
    }

    public List<GroupStageInfo> createAll(List<GroupStageInfo> groupStageInfos) {
        return groupStageInfoRepository.saveAll(groupStageInfos);
    }

    @Transactional
    public void delete(GroupStageInfo groupStageInfo) {
        groupStageInfoRepository.delete(groupStageInfo);
    }

    @Transactional
    public void deleteByTournamentID(Integer tournamentID) {
        groupStageInfoRepository.deleteByTournamentID(tournamentID);
    }

    public List<GroupStageInfo> getByTournamentAndGroupTitle(Integer tournamentID, String groupTitle) {
        return groupStageInfoRepository.findByTournamentIDAndGroupTitle(tournamentID, groupTitle).orElse(null);
    }

    public List<GroupStageInfo> getByTournamentAndRank(Integer tournamentID, Integer rank) {
        return groupStageInfoRepository.findByTournamentIDAndRank(tournamentID, rank).orElse(null);
    }

    public List<GroupStageInfo> getByTournamentAndBestThird(Integer tournamentID, Integer bestThird) {
        return groupStageInfoRepository.findByTournamentIDAndBestThird(tournamentID, bestThird).orElse(null);
    }

    public GroupStageInfo getByTournamentAndTeam(Integer tournamentID, Integer teamID) {
        return groupStageInfoRepository.findByTournamentIDAndTeamID(tournamentID, teamID).orElse(null);
    }

    public List<GroupStageInfo> getRankingsByGroupTitle(Integer tournamentID, String groupTitle) {
        return groupStageInfoRepository.findRankingsByTournamentIDAndGroupTitle(tournamentID, groupTitle).orElse(null);
    }

    public List<GroupStageInfo> getRankingsBySortedRanks(Integer tournamentID, Integer rank) {
        return groupStageInfoRepository.findGroupStageInfosSortedAndRanks(tournamentID, rank).orElse(null);
    }

    public List<GroupStageInfo> getByTournamentAndExcludeRanks(Integer tournamentID, List<Integer> ranks) {
        return groupStageInfoRepository.findByTournamentIDAndRankNotIn(tournamentID, ranks).orElse(null);
    }

    public List<GroupStageInfo> findAll() {
        return groupStageInfoRepository.findAll();
    }

}