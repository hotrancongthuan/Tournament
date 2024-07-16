package com.duan2.tournamentTDTU.draw;

import com.duan2.tournamentTDTU.models.*;
import com.duan2.tournamentTDTU.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.*;
import java.util.concurrent.CompletableFuture;

@Service
public class DrawMatchService {

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchService matchService;
    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private SchoolYearService schoolYearService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private RoundScheduleService roundScheduleService;

    @Autowired
    private GroupStageInfoService groupStageInfoService;

    @Autowired
    private AttendanceService attendanceService;

    public Tournament getTournamentCurrent(){
        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
        Tournament tournament;
        if (schoolYear != null){
            tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());
            if(tournament != null){
                return tournament;
            }
        }
        return null;
    }

    @Async("progressFile")
    public CompletableFuture<Void> createDraw(){

        return CompletableFuture.runAsync(() -> {
            transactionTemplate.execute(status -> {
                roundScheduleService.deleteAllByTournament(getTournamentCurrent().getID());
                matchService.deleteAllByTournament(getTournamentCurrent().getID());
                List<Team> teams = teamService.getTeamsByStatus(getTournamentCurrent().getID(),"join");
                for(Team team : teams){
                    team.setGroupStage("");
                }
                System.out.println("createDraw ");

                int indexPlayOff = 0;
                int totalTeam = teams.size();

                List<List<Match_>> matchsIndex = new ArrayList<>();

                DrawMatch drawMatch = new DrawMatch();
                LinkedHashMap<String,Integer> numberFinalMatch = drawMatch.DrawFinal();
                Set<String> finalRounds = numberFinalMatch.keySet();

                int newRoundFinal = 0;
                boolean thirdPlace = true;
                for (String finalRound: finalRounds){
                    List<Match_> finalMatchIndex = new ArrayList<>();
                    RoundSchedule roundSchedule = new RoundSchedule();
                    roundSchedule.setTournament(getTournamentCurrent());
                    roundSchedule.setRoundTitle(finalRound);
                    roundSchedule.setNumberMatchDone(0);
                    roundSchedule.setNumberMatch(numberFinalMatch.get(finalRound));
                    roundScheduleService.createRoundSchedule(roundSchedule);

                    for (int index = 0; index < numberFinalMatch.get(finalRound); index++) {
                        Match_ match = new Match_();
                        match.setIndexMatch(String.valueOf(indexPlayOff));
                        if(finalRound.equals("final")){
                            if(thirdPlace){
                                match.setType("third-place");
                                thirdPlace = false;
                            }else{
                                match.setType(finalRound);
                            }
                        }else{
                            match.setType(finalRound);
                        }
                        match.setStatus("notyet");
                        match.setTournament(getTournamentCurrent());
                        if(matchsIndex.size() > 1){
                            String nextMatchIndex = matchsIndex.get(newRoundFinal - 1).get(index/2).getIndexMatch();
                            match.setNextMatchIndex(nextMatchIndex);
                        }
                        finalMatchIndex.add(match);
                        indexPlayOff = indexPlayOff + 1;
                    }
                    matchsIndex.add(finalMatchIndex);
                    newRoundFinal = newRoundFinal + 1;
                }

                LinkedHashMap<String,Integer> numberPlayoffMatch = drawMatch.DrawGroupStage(totalTeam);
                Set<String> playOffRounds = numberPlayoffMatch.keySet();
                Random random = new Random();

                if(totalTeam >= 24){
                    int numberGroup = 0;
                    int numberTeamInGroup = 0;
                    if(totalTeam >= 32){
                        numberGroup = 8;
                        numberTeamInGroup = 4;
                    } else if (totalTeam >= 28) {
                        numberGroup = 7;
                        numberTeamInGroup = 4;
                    } else{
                        numberGroup = 8;
                        numberTeamInGroup = 3;
                    }

                    // vòng bảng
                    // A1 A2 A3 A4 ,... H1 H2 H3 H4
                    String[][] groups = new String[numberGroup][numberTeamInGroup];
                    for (int i = 0; i < numberGroup; i++) {
                        for (int j = 0; j < numberTeamInGroup; j++) {
                            char letter = (char) ('A' + i);
                            groups[i][j] = String.valueOf(letter) + (j + 1);
                        }
                    }

                    List<Match_> groupMatchIndex = new ArrayList<>();
                    for (int i = 0; i < numberGroup; i++) {
                        for (int x = 0; x < groups[i].length - 1; x++) {
                            for (int y = x + 1; y < groups[x].length; y++) {
                                Match_ match = new Match_();
                                match.setIndexMatch(groups[i][x] +"-"+ groups[i][y]);
                                match.setType("group-stage-"+groups[i][0].substring(0, 1));
                                match.setStatus("notyet");
                                match.setTournament(getTournamentCurrent());
                                groupMatchIndex.add(match);
                                indexPlayOff = indexPlayOff + 1;
                            }
                        }
                    }
                    matchsIndex.add(groupMatchIndex);

                    RoundSchedule roundSchedule = new RoundSchedule();
                    roundSchedule.setTournament(getTournamentCurrent());
                    roundSchedule.setRoundTitle("group-stage");
                    roundSchedule.setNumberMatchDone(0);
                    roundSchedule.setNumberMatch(groupMatchIndex.size());
                    roundScheduleService.createRoundSchedule(roundSchedule);

                    // playoff
                    // từ ck đến vòng bảng có 5 vòng
                    int newRound = 5;
                    for(String playOff : playOffRounds){
                        RoundSchedule roundSchedule1 = new RoundSchedule();
                        roundSchedule1.setTournament(getTournamentCurrent());
                        roundSchedule1.setRoundTitle(playOff);
                        roundSchedule1.setNumberMatchDone(0);
                        roundSchedule1.setNumberMatch(numberPlayoffMatch.get(playOff));
                        roundScheduleService.createRoundSchedule(roundSchedule1);

                        List<Match_> playOffMatchIndex = new ArrayList<>();
                        List<Match_> nextListMatch = new ArrayList<>();
                        List<String> selectGroup = new ArrayList<>();
                        if(matchsIndex.size() > 5){
                            nextListMatch  = matchsIndex.get(newRound - 1);
                        }else{
                            for (int i = 0; i < numberGroup; i++) {
                                for (int j = 1; j <= numberTeamInGroup; j++) {
                                    char Group = (char)('A' + i);
                                    selectGroup.add(String.valueOf(Group) + j);
                                }
                            }
                        }

                        for (int index = 0; index < numberPlayoffMatch.get(playOff); index++) {
                            Match_ match = new Match_();
                            match.setIndexMatch(String.valueOf(indexPlayOff));
                            match.setType(playOff);
                            match.setStatus("notyet");
                            match.setTournament(getTournamentCurrent());

                            // nếu số vòng lớn hơn 5 có từ 2 vòng play off
                            if(matchsIndex.size() > 5){
                                // playoff có vòng sau là playoff
                                // bốc thăm bảng đấu cho đội playoff
                                int indexNextMatch = random.nextInt(nextListMatch.size());
                                String nextMatchIndex = nextListMatch.remove(indexNextMatch).getIndexMatch();
                                match.setNextMatchIndex(nextMatchIndex);
                            }else {
                                // playoff cuối bước vào vòng bảng
                                // bốc thăm bảng đấu cho đội không thi đấu playoff
                                int randomIndexGroup = random.nextInt(selectGroup.size());
                                match.setNextMatchIndex(selectGroup.remove(randomIndexGroup));
                            }
                            playOffMatchIndex.add(match);
                            indexPlayOff = indexPlayOff + 1;
                        }
                        matchsIndex.add(playOffMatchIndex);
                        newRound = newRound + 1;
                    }
                }else{
                    // playoff không có vòng bảng
                    if(playOffRounds.size() > 0){
                        int newRound = 4;
                        for(String playOff : playOffRounds){
                            RoundSchedule roundSchedule1 = new RoundSchedule();
                            roundSchedule1.setTournament(getTournamentCurrent());
                            roundSchedule1.setRoundTitle(playOff);
                            roundSchedule1.setNumberMatchDone(0);
                            roundSchedule1.setNumberMatch(numberPlayoffMatch.get(playOff));
                            roundScheduleService.createRoundSchedule(roundSchedule1);

                            List<Match_> playOffMatchIndex = new ArrayList<>();
                            List<Match_> nextListMatch = matchsIndex.get(newRound - 1);
                            for (int index = 0; index < numberPlayoffMatch.get(playOff); index++) {
                                Match_ match = new Match_();
                                match.setIndexMatch(String.valueOf(indexPlayOff));
                                match.setType(playOff);
                                match.setStatus("notyet");
                                match.setTournament(getTournamentCurrent());
                                int indexNextMatch = random.nextInt(nextListMatch.size());
                                String nextMatchIndex = nextListMatch.remove(indexNextMatch).getIndexMatch();
                                match.setNextMatchIndex(nextMatchIndex);
                                playOffMatchIndex.add(match);
                                indexPlayOff = indexPlayOff + 1;
                            }
                            matchsIndex.add(playOffMatchIndex);
                            newRound = newRound + 1;
                        }
                    }
                }

                // bốc thăm
                int roundTitle = matchsIndex.size();

                boolean draw = true;
//                boolean done = true;

                // lưu các trận đã có đội bắt cặp <matchID, số đội đã bắt căp>
                Map<String,Integer> nextMatchList = new HashMap<>();

                while (teams.size() > 0){
                    // lấy các trận vòng cuối
                    List<Match_> matchs = matchsIndex.get(roundTitle - 1);
//                    int countNextMatch = 0;
                    for(Match_ match: matchs){
                        if(match.getType().contains("playoff")){
                            String nextMatch = match.getNextMatchIndex();
                            if(!nextMatchList.keySet().contains(nextMatch)){
                                nextMatchList.put(nextMatch,1);
                            }else{
                                int tam = nextMatchList.get(nextMatch) + 1;
                                nextMatchList.put(nextMatch,tam);
                            }
                            String indexMatch = match.getIndexMatch();
                            if(nextMatchList.keySet().contains(indexMatch)){
                                if(nextMatchList.get(indexMatch) == 1){
                                    int teamIndex = random.nextInt(teams.size());
                                    Team teamChoice = teams.remove(teamIndex);
                                    if(match.getTeam1() != null)
                                        match.setTeam1(teamChoice);
                                    else if(match.getTeam2() != null)
                                        match.setTeam2(teamChoice);
                                }
                            }else {
                                int team1Index = random.nextInt(teams.size());
                                Team team1Choice = teams.remove(team1Index);
                                match.setTeam1(team1Choice);
                                int team2Index = random.nextInt(teams.size());
                                Team team2Choice = teams.remove(team2Index);
                                match.setTeam2(team2Choice);
                            }
                        }else if(match.getType().contains("group-stage")){

                            if(draw){
                                // chia bảng
                                int numberGroup = 0;
                                int numberTeamInGroup = 0;
                                if(totalTeam >= 32){
                                    numberGroup = 8;
                                    numberTeamInGroup = 4;
                                } else if (totalTeam >= 28) {
                                    numberGroup = 7;
                                    numberTeamInGroup = 4;
                                } else{
                                    numberGroup = 8;
                                    numberTeamInGroup = 3;
                                }

                                List<String> groupTitle = new ArrayList<>();
                                for (int i = 0; i < numberGroup; i++) {
                                    for (int j = 0; j < numberTeamInGroup; j++) {
                                        char letter = (char) ('A' + i);
                                        groupTitle.add(String.valueOf(letter) + (j + 1));
                                    }
                                }

                                Set<String> nextMatchListTitle = nextMatchList.keySet();
                                groupTitle.removeAll(nextMatchListTitle);

                                // chia bảng cho các đội
                                for(Team team : teams){
                                    int indexGroup = random.nextInt(groupTitle.size());
                                    GroupStageInfo groupStageInfo = new GroupStageInfo();
                                    groupStageInfo.setGroupTitle(groupTitle.get(indexGroup).substring(0,1));
                                    groupStageInfo.setTournament(getTournamentCurrent());
                                    groupStageInfo.setTeam(team);
                                    groupStageInfoService.create(groupStageInfo);
                                    team.setGroupStage(groupTitle.remove(indexGroup));
                                }
                                teamService.createAllTeam(teams);

                                draw = false;
                            }

                            String[] groupIndex = match.getIndexMatch().split("-");

                            if(!nextMatchList.keySet().contains(groupIndex[0])){
                                Team team = teamService.getTeamsByGroupStage(getTournamentCurrent().getID(),groupIndex[0]);
                                if(team != null){
                                    if(match.getTeam1() == null){
                                        match.setTeam1(team);
                                    }else {
                                        match.setTeam2(team);
                                    }
                                }
                                teams.remove(team);
                            }

                            if(!nextMatchList.keySet().contains(groupIndex[1])){
                                Team team = teamService.getTeamsByGroupStage(getTournamentCurrent().getID(),groupIndex[1]);
                                if(team != null){
                                    if(match.getTeam2() == null){
                                        match.setTeam2(team);
                                    }else {
                                        match.setTeam1(team);
                                    }
                                }
                                teams.remove(team);
                            }

//                            done = false;
                        }else {
                            String indexMatch = match.getIndexMatch();
                            if(nextMatchList.keySet().contains(indexMatch)){
                                if(nextMatchList.get(indexMatch) == 1){
                                    int team1Index = random.nextInt(teams.size());
                                    Team team1Choice = teams.remove(team1Index);
                                    match.setTeam1(team1Choice);
                                }
                            }else {
                                int team1Index = random.nextInt(teams.size());
                                Team team1Choice = teams.remove(team1Index);
                                match.setTeam1(team1Choice);
                                int team2Index = random.nextInt(teams.size());
                                Team team2Choice = teams.remove(team2Index);
                                match.setTeam2(team2Choice);
                            }
                        }
                    }
                    roundTitle = roundTitle - 1;
                }

                for (int i = 0; i < matchsIndex.size(); i++) {
                    matchService.createAllMatch(matchsIndex.get(i));
                }

                List<RoundSchedule> roundSchedules = roundScheduleService.getAllRoundSchedule(getTournamentCurrent().getID());

                roundSchedules.get(roundSchedules.size()-1).setScheduleStatus("inprogress");
                for (int i = roundSchedules.size() - 2; i >= 0; i--) {
                    roundSchedules.get(i).setScheduleStatus("notyet");
                }
                roundScheduleService.createAllRoundSchedule(roundSchedules);

                return null;
            });
        });
    }
}
