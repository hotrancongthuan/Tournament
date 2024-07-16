package com.duan2.tournamentTDTU.repositorys;


import com.duan2.tournamentTDTU.models.GroupStageInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GroupStageInfoRepository extends JpaRepository<GroupStageInfo, Integer> {

    void deleteByTournamentID(Integer tournamentID);

    Optional<List<GroupStageInfo>> findByTournamentIDAndGroupTitle(Integer tournamentID, String groupTitle);

    Optional<List<GroupStageInfo>> findByTournamentIDAndRank(Integer tournamentID, Integer rank);

    @Query("SELECT g FROM GroupStageInfo g WHERE g.tournament.ID = :tournamentID AND g.rank NOT IN :ranks")
    Optional<List<GroupStageInfo>> findByTournamentIDAndRankNotIn(@Param("tournamentID") Integer tournamentID,@Param("ranks") List<Integer> ranks);

    Optional<List<GroupStageInfo>> findByTournamentIDAndBestThird(Integer tournamentID, Integer bestThird);

    Optional<GroupStageInfo> findByTournamentIDAndTeamID(Integer tournamentID, Integer teamID);

    @Query("SELECT g FROM GroupStageInfo g WHERE g.tournament.ID = :tournamentID AND g.groupTitle = :groupTitle ORDER BY g.point DESC, g.goalsDifference DESC, g.redCard ASC, g.yellowCard ASC")
    Optional<List<GroupStageInfo>> findRankingsByTournamentIDAndGroupTitle(@Param("tournamentID")Integer tournamentID,@Param("groupTitle") String groupTitle);

    @Query("SELECT g FROM GroupStageInfo g WHERE g.tournament.id = :tournamentId AND g.rank = :rank ORDER BY g.point DESC, g.goalsDifference DESC, g.redCard ASC, g.yellowCard ASC")
    Optional<List<GroupStageInfo>> findGroupStageInfosSortedAndRanks(@Param("tournamentId") Integer tournamentId, @Param("rank") Integer rank);
}