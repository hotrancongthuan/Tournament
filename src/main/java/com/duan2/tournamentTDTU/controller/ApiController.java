package com.duan2.tournamentTDTU.controller;

import com.duan2.tournamentTDTU.constants.Shift;
import com.duan2.tournamentTDTU.models.*;
import com.duan2.tournamentTDTU.services.*;
import com.duan2.tournamentTDTU.updateMatch.CardData;
import com.duan2.tournamentTDTU.updateMatch.DataModel;
import com.duan2.tournamentTDTU.updateMatch.PenaltyData;
import com.duan2.tournamentTDTU.updateMatch.ScoreData;
import com.fasterxml.jackson.core.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
public class ApiController {


    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TournamentTakeInService tournamentTakeInService;

    @Autowired
    private SchoolYearService schoolYearService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private SemesterWeekService semesterWeekService;

    @Autowired
    private ScheduleSubjectService scheduleSubjectService;

    @Autowired
    private ScheduleStudentService scheduleStudentService;

    @Autowired
    private PlanYearService planYearService;

    @Autowired
    private RoundScheduleService roundScheduleService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LivescoreService livescoreService;
    @Autowired
    private LiveCardService liveCardService;
    @Autowired
    private LivePenaltyService livePenaltyService;
    @Autowired
    private GroupStageInfoService groupStageInfoService;
    @GetMapping("/api/getStudentSearch")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> getAllClass(){
        Set<String> takeIns = studentService.getDistinctTakeIn();
        Set<String> class_ = studentService.getDistinctClass();
        Map<String, Object> response = new HashMap<>();
        response.put("takeIns", takeIns);
        response.put("class_", class_);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/getClassListByTakeIn")
    @PreAuthorize("hasRole('MANAGER')")
    public ResponseEntity<Map<String, Object>> getClassByTakeIn(@RequestBody Map<String, String> TakeInID){
        Set<String> class_ = studentService.getDistinctClassByTakeIn(TakeInID.get("data"));
        Map<String, Object> response = new HashMap<>();
        response.put("class_", class_);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/api/getStudentsSignUp")
    @PreAuthorize("hasAnyRole('STUDENT','PLAYER')")
    public ResponseEntity<?> getStudentsSignUpByClass(@RequestBody  Map<String, String> data){

        String classID = data.get("data");

        List<Student> students = studentService.getStudentByClass(classID);
        if(students.isEmpty()){
            return null;
        }

        String studentTakeInCheck =  students.get(0).getTakeIn().getID();

        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
        Tournament tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());

        Set<TournamentTakeIn> tournamentTakeIns = tournament.getTournamentTakeIns();
        Set<String> checkTakeIn = tournamentTakeIns.stream()
                .map(tournamentTakeIn -> tournamentTakeIn.getTakeIn().getID())
                .collect(Collectors.toSet());

        if(!checkTakeIn.contains(studentTakeInCheck)){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Lớp không thể tham gia giải đấu");
        }

        List<StudentInfo> studentInfos = new ArrayList<>();
        for(Student student: students){
            User user = userService.getUserById(student.getID());
            if(user.getGender().equals("male")){
                StudentInfo studentInfo = new StudentInfo(student.getID(),user.getName(),student.getTakeIn(),student.getClassID(),student.getMode());
                studentInfos.add(studentInfo);
            }
        }

        return new ResponseEntity<>(studentInfos, HttpStatus.OK);
    }

    @GetMapping("/api/getLimitPlayerInTeam")
    @PreAuthorize("hasAnyRole('STUDENT','PLAYER')")
    public ResponseEntity<?> getLimitPlayerInTeam(){

        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
        Tournament tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());

        Integer maxPlayerInTeam = tournament.getMaxPlayerPerTeam();
        Integer minPlayerInTeam = tournament.getMinPlayerPerTeam();

        Map<String, Integer> limitPlayer = new HashMap<>();
        limitPlayer.put("maxPlayer",maxPlayerInTeam);
        limitPlayer.put("minPlayer",minPlayerInTeam);

        return new ResponseEntity<>(limitPlayer, HttpStatus.OK);
    }

    @PostMapping("/api/aceptPlayers")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> aceptPlayers(
            @RequestBody  Map<String, RefusePlayers> data,
            @ModelAttribute("tournament_current") Tournament tournament){

        if(data.get("data") == null){
            return null;
        }

        RefusePlayers aceptPlayers = data.get("data");

        List<String> players = aceptPlayers.getPlayers();
        Integer teamID = Integer.parseInt(aceptPlayers.getTeamID());
        Team team = teamService.getTeamById(teamID);

        Integer numberPlayerOfTeam = team.getTotalPlayer();

        if(players == null || numberPlayerOfTeam != players.size()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }

        team.setStatus("join");
        team.setStop(1);
        teamService.createTeam(team);

        if(players != null){
            for (String refusePlayerID : players){
                Player player = playerService.getPlayerByStudentIDAndTournamentID(refusePlayerID,tournament.getID());
                player.setStatus("signup");
                player.setBannedNextMatch(0);
                playerService.createPlayer(player);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/api/refusePlayers")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> refusePlayers(@RequestBody  Map<String, RefusePlayers> data,
                                           @ModelAttribute("tournament_current") Tournament tournament){

        if(data.get("data") == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        RefusePlayers refusePlayers = data.get("data");

        List<String> players = refusePlayers.getPlayers();
        Integer teamID = Integer.parseInt(refusePlayers.getTeamID());

        if(teamID == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        Team team = teamService.getTeamById(teamID);

        if(team == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        team.setStatus("refuse");
        team.setStop(0);
        teamService.createTeam(team);

        if(players != null){
            for (String refusePlayerID : players){
                Player player = playerService.getPlayerByStudentIDAndTournamentID(refusePlayerID, tournament.getID());
                player.setStatus("refuse");
                playerService.createPlayer(player);
            }
        }else{
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @GetMapping("/api/closeSignup")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> closeSignup(@ModelAttribute("tournament_current") Tournament tournament){
        List<Team> teams = teamService.getAllTeams(tournament.getID());
        for(Team team: teams){
            if(team.getStatus().equals("signup")){
                return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
            }
        }
        tournament.setStatus("schedule");
        tournamentService.createTournament(tournament);

        for(Team team: teams){
            if(team.getStatus().equals("join")){
                List<Player> players = playerService.getPlayerByTeam(team.getID());
                for(Player player: players){
                    player.setStatus("play");
//                    playerService.createPlayer(player);
                }
                playerService.createAllPlayer(players);
            } else if (team.getStatus().equals("refuse")) {
                List<Player> players = playerService.getPlayerByTeam(team.getID());
                for(Player player: players){
                    player.setStatus("refuse");
//                    playerService.createPlayer(player);
                }
                playerService.createAllPlayer(players);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("");
    }


    @PostMapping("/api/openSignup")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> openSignup(@ModelAttribute("tournament_current") Tournament tournament,@RequestBody  Map<String, String> data){

        LocalDate endDate = LocalDate.parse(data.get("endDate"));
        tournament.setRegisterEndDate(endDate);
        tournament.setStatus("open");
        tournamentService.createTournament(tournament);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/api/cancelSignup")
    @PreAuthorize("hasAnyRole('PLAYER')")
    public ResponseEntity<?> cancelSignup(@RequestBody  Map<String, String> data){
        Integer teamID = Integer.parseInt(data.get("data"));
        Team team = teamService.getTeamById(teamID);
        List<Player> players = playerService.getPlayerByTeam(teamID);
        for(Player player: players){
            Account account = accountService.getAccountById(player.getStudentID());
            account.setRole("ROLE_STUDENT");
            accountService.save(account);
        }
        playerService.deletePlayers(players);
        teamService.deleteTeam(team);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/api/cancelPlayers")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> cancelPlayers(
            @RequestBody  Map<String, CancelPlayers> data,
            @ModelAttribute("tournament_current") Tournament tournament
            ){

        if(data.get("data") == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        CancelPlayers cancelPlayers = data.get("data");

        List<String> cancelPlayerList = cancelPlayers.getCancelPlayers();
        List<String> aceptPlayerList = cancelPlayers.getAceptPlayers();

        if(aceptPlayerList != null){
            if(aceptPlayerList.size() < 5){
                Map<String , String> error = new HashMap<>();
                error.put("messager","Số cầu thủ còn lại không đủ thành viên thi đấu, nếu vẫn loại các cầu thủ này vui lòng loại toàn đội");
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
            }
            for (String aceptPlayerID : aceptPlayerList){
                Player player = playerService.getPlayerByStudentIDAndTournamentID(aceptPlayerID, tournament.getID());
                if(player == null){
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
                }
                player.setStatus("play");
                playerService.createPlayer(player);
            }
        }

        if(cancelPlayerList != null){
            for (String cancelPlayerID : cancelPlayerList){
                Player player = playerService.getPlayerByStudentIDAndTournamentID(cancelPlayerID, tournament.getID());
                if(player == null){
                    return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
                }
                player.setStatus("cancel");
                playerService.createPlayer(player);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/api/cancelTeam")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> cancelTeam(
            @RequestBody  Map<String, String> data,
            @ModelAttribute("tournament_current") Tournament tournament
    ){

        if(data.get("data") == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        Integer teamID = Integer.parseInt(data.get("data"));

        Team team = teamService.getTeamById(teamID);

        if(team == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        team.setStatus("cancel");
        team.setStop(0);
        teamService.createTeam(team);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/api/aceptTeamInprogress")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> aceptTeamInprogress(
            @RequestBody  Map<String, String> data,
            @ModelAttribute("tournament_current") Tournament tournament
    ){

        if(data.get("data") == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        Integer teamID = Integer.parseInt(data.get("data"));

        Team team = teamService.getTeamById(teamID);

        if(team == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        team.setStatus("join");
        team.setStop(1);
        teamService.createTeam(team);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/api/changeCaptain")
    @PreAuthorize("hasAnyRole('PLAYER')")
    public ResponseEntity<?> changeCaptain(@RequestBody  Map<String, String> data){
        if(data.get("current_captain") == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        if(data.get("new_captain") == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        Integer captainCurrentID = Integer.parseInt(data.get("current_captain"));
        Integer newCaptainID = Integer.parseInt(data.get("new_captain"));

        Player captainCurrent = playerService.getPlayerByID(captainCurrentID);
        Player newCaptain = playerService.getPlayerByID(newCaptainID);

        if(captainCurrent == null){
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        } else if (newCaptain == null) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body("");
        }

        captainCurrent.setCaptain(0);
        newCaptain.setCaptain(1);
        playerService.createPlayer(captainCurrent);
        playerService.createPlayer(newCaptain);

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/api/getInfoMatch")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> getInfoMatch(@RequestBody  Map<String, String> data){
        Integer matchID = Integer.parseInt(data.get("data"));
        Match_ match = matchService.getMatchById(matchID);
        if(match == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }

        return new ResponseEntity<>(match, HttpStatus.OK);
    }

    private Shift findShiftChangeDateTime(LocalTime time){
        for (Shift shift: Shift.values()){
            LocalTime start = LocalTime.parse(shift.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(shift.getEndTime(),DateTimeFormatter.ofPattern("HH:mm"));
            if ((time.equals(start) || time.isAfter(start)) && time.isBefore(end)) {
                return shift;
            }
        }
        return null;
    }

    private Tournament getTournamentCurrent(){
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

    private SchoolYear getSchoolYearCurrent(){
        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
        if (schoolYear != null){
            return schoolYear;
        }
        return null;
    }

    @PostMapping("/api/changeInfoMatchCheck")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> changeInfoMatchCheck(@RequestBody  Map<String, String> data){
        Integer matchID = Integer.parseInt(data.get("matchID"));
        LocalDate changeDate = LocalDate.parse(data.get("changeDate"));
        LocalTime changeTime = LocalTime.parse(data.get("changeTime"));

        RoundSchedule roundSchedule = roundScheduleService.getRoundScheduleByStatus(getTournamentCurrent().getID(), "inprogress").get(0);
        LocalDate limitDateStart = roundSchedule.getDateStart();
        LocalDate limitDateEnd = roundSchedule.getDateEnd();
        if(changeDate.isBefore(limitDateStart) || changeDate.isAfter(limitDateEnd)){
            Map<String, Object> response = new HashMap<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            response.put("errorMessager", "Chọn ngày thi đấu vòng "+roundSchedule.getRoundTitle()+" từ "+roundSchedule.getDateStart().format(formatter)+" đến "+roundSchedule.getDateEnd().format(formatter));
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }

        Match_ match = matchService.getMatchById(matchID);
        if(match == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }

        Tournament tournament = getTournamentCurrent();

        Map<String,String> error = new HashMap<>();

        List<String> studentsSchedulErrorTeam1 = new ArrayList<>();
        List<String> studentsSchedulErrorTeam2 = new ArrayList<>();

        // ngày trong tuần
        DayOfWeek dayOfWeek = changeDate.getDayOfWeek();
        int weekdayRaw = dayOfWeek.getValue();
        int weekday = weekdayRaw == 7 ? 1 : weekdayRaw + 1;

        // ca học
        Shift shift = findShiftChangeDateTime(changeTime);


        if(shift != null){
            Integer shiftNumber = shift.getShiftNumber();

            // tuần học
            SemesterWeek semesterWeek = semesterWeekService.getSemesterWeekByDate(getSchoolYearCurrent().getID(),changeDate);
            String week = semesterWeek.getNumWeek();

            // kiểm tra lịch học sinh viên
            List<ScheduleSubject> subjectsHappenning = scheduleSubjectService.getScheduleSubjectsByShiftAndWeekdayAndWeek(getSchoolYearCurrent().getID(),shiftNumber,weekday,"-"+week+"-");

            Team team1 = teamService.getTeamById(match.getTeam1().getID());
            Team team2 = teamService.getTeamById(match.getTeam2().getID());
            for(ScheduleSubject scheduleSubject : subjectsHappenning){
                List<Player> playersTeam1 = playerService.getPlayerByTeam(team1.getID());
                List<String> studentIDs1 = playersTeam1.stream().map(Player::getStudentID).collect(Collectors.toList());
                List<ScheduleStudent> scheduleStudents1 = scheduleStudentService.getScheduleStudentByScheduleSubjectIDAndStudentID(scheduleSubject.getSubjectID(),studentIDs1);
                if(scheduleStudents1 != null){
                    List<String> studentIDsError = scheduleStudents1.stream()
                            .map(ScheduleStudent::getStudent)
                            .filter(Objects::nonNull)
                            .map(Student::getID)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    if(team1.getTotalPlayer() - scheduleStudents1.size() < 5){
                        String team1Name = !team1.getClass2().isEmpty() ? team1.getClass1() +" - "+ team1.getClass2() : team1.getClass1();
                        String errorNoti = "Đội "+team1Name+" Không đủ thành viên do thành viên trùng lịch học: "+studentIDsError.toString();
                        Map<String, Object> response = new HashMap<>();
                        response.put("errorMessager", errorNoti);
                        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
                    }else{
                        // danh sách sinh viên trùng lịch học nhưng đủ 5 người thi đấu
                        studentsSchedulErrorTeam1.addAll(studentIDsError);
                    }
                }

                List<Player> playersTeam2 = playerService.getPlayerByTeam(team2.getID());
                List<String> studentIDs2 = playersTeam2.stream().map(Player::getStudentID).collect(Collectors.toList());
                List<ScheduleStudent> scheduleStudents2 = scheduleStudentService.getScheduleStudentByScheduleSubjectIDAndStudentID(scheduleSubject.getSubjectID(),studentIDs2);
                if(scheduleStudents2 != null){
                    List<String> studentIDsError = scheduleStudents2.stream()
                            .map(ScheduleStudent::getStudent)
                            .filter(Objects::nonNull)
                            .map(Student::getID)
                            .filter(Objects::nonNull)
                            .collect(Collectors.toList());
                    if(team2.getTotalPlayer() - scheduleStudents2.size() < 5){
                        Map<String, Object> response = new HashMap<>();
                        String team2Name = !team2.getClass2().isEmpty() ? team2.getClass1() +" - "+ team2.getClass2() : team2.getClass1();
                        String errorNoti = "Đội "+team2Name+" Không đủ thành viên, thành viên trùng lịch học: "+studentIDsError.toString();
                        response.put("errorMessager", errorNoti);
                        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
                    }else{
                        // danh sách sinh viên trùng lịch học nhưng đủ 5 người thi đấu
                        studentsSchedulErrorTeam2.addAll(studentIDsError);
                    }
                }
            }
            if(studentsSchedulErrorTeam1.size() > 0){
                String team1Name = !team1.getClass2().isEmpty() ? team1.getClass1() +" - "+ team1.getClass2() : team1.getClass1();
                error.put("team1Schedule","Thành viên đội "+team1Name+" trùng lịch: "+studentsSchedulErrorTeam1.toString());
            }
            if(studentsSchedulErrorTeam2.size() > 0){
                String team2Name = !team2.getClass2().isEmpty() ? team2.getClass1() +" - "+ team2.getClass2() : team2.getClass1();
                error.put("team2Schedule","Thành viên đội "+team2Name+" trùng lịch: "+studentsSchedulErrorTeam2.toString());
            }

            // kiểm tra trùng ngày nghỉ
            PlanYear planYear = planYearService.getPlanYearByWeek(getSchoolYearCurrent().getID(), week);
            if(planYear != null){
                LocalDate startPlanYear = planYear.getDate();
                LocalDate endPlanYear = startPlanYear.plusDays(planYear.getNumDate()-1);
                if(changeDate.equals(startPlanYear) || (changeDate.isAfter(startPlanYear) && changeDate.isBefore(endPlanYear)) || changeDate.equals(endPlanYear)){
                    error.put("errorMessager","Trùng lịch nghỉ "+planYear.getName()+" "+planYear.getDate());
                    return new ResponseEntity<>(error, HttpStatus.CONFLICT);
                }
            }

            int time1Match = tournament.getTimeHalf() * 2 + tournament.getTimeBreak();
//            LocalTime checkTimeStart = changeTime;
//            LocalTime checkTimeEnd = changeTime.plusMinutes(time1Match);

            List<Match_> matchCheckList = matchService.getMatchByDateAndTime(getTournamentCurrent().getID(), changeDate, changeTime);
            if(matchCheckList != null){
                // kiểm tra trùng lịch thi đấu 2 đội
                for (Match_ matchCheck : matchCheckList){
                    int team1Check = matchCheck.getTeam1().getID();
                    int team2Check = matchCheck.getTeam2().getID();
                    if(matchCheck.getID().equals(matchID)){
                        if( team1.getID() == team1Check ||
                                team1.getID() == team2Check){
                            String team1Name = !team1.getClass2().isEmpty() ? team1.getClass1() +" - "+ team1.getClass2() : team1.getClass1();
                            Map<String, Object> response = new HashMap<>();
                            response.put("errorMessager","Đội "+team1Name+" trùng lịch thi đấu");
                            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
                        }
                        if(team2.getID() == team1Check ||
                                team2.getID() == team2Check){
                            String team2Name = !team2.getClass2().isEmpty() ? team2.getClass1() +" - "+ team2.getClass2() : team2.getClass1();
                            Map<String, Object> response = new HashMap<>();
                            response.put("errorMessager","Đội "+team2Name+" trùng lịch thi đấu");
                            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
                        }
                    }
                }
                // kiểm tra số trận cùng lúc
                if(matchCheckList.size()  >= tournament.getMatchSameTime() ){
                    error.put("sameTimeMatch","Có "+matchCheckList.size()+" trận cùng diễn ra cùng lúc");
                }
            }

            if(error.keySet().size() > 0){
                return new ResponseEntity<>(error, HttpStatus.CONFLICT);
            }
        }

        error.put("success","Không có lỗi xảy ra");

        return new ResponseEntity<>(error, HttpStatus.OK);
    }

    @PostMapping("/api/changeInfoMatch")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> changeInfoMatch(@RequestBody  Map<String, String> data){
        Integer matchID = Integer.parseInt(data.get("matchID"));
        LocalDate changeDate = LocalDate.parse(data.get("changeDate"));
        LocalTime changeTime = LocalTime.parse(data.get("changeTime"));
        Match_ match = matchService.getMatchById(matchID);
        if(match == null){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }
        match.setTime(changeTime);
        match.setDate(changeDate);
        matchService.createMatch(match);
        return ResponseEntity.status(HttpStatus.OK).body("");
    }

    @PostMapping("/api/getPlayerAttendance")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> getPlayerAttendance(@RequestBody  Map<String, String> data){
        Integer matchID = Integer.parseInt(data.get("matchID"));
        Integer team1ID = Integer.parseInt(data.get("team1ID"));
        Integer team2ID = Integer.parseInt(data.get("team2ID"));
        if(matchID == null || team1ID == null || team2ID == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        List<Attendance> playerMainTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(matchID,team1ID,"main");
        if(playerMainTeam1 == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        List<Player> playersMainTeam1 = playerMainTeam1.stream()
                .map(Attendance::getPlayer)
                .collect(Collectors.toList());

        List<Attendance> playerSubTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(matchID,team1ID,"sub");
        if(playerSubTeam1 == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
        }

        List<Player> playersSubTeam1 = playerSubTeam1.stream()
                .map(Attendance::getPlayer)
                .collect(Collectors.toList());

        playersMainTeam1.addAll(playersSubTeam1);

        List<Attendance> playerMainTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(matchID,team2ID,"main");
        List<Player> playersMainTeam2 = playerMainTeam2.stream()
                .map(Attendance::getPlayer)
                .collect(Collectors.toList());
        List<Attendance> playerSubTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(matchID,team2ID,"sub");
        List<Player> playersSubTeam2 = playerSubTeam2.stream()
                .map(Attendance::getPlayer)
                .collect(Collectors.toList());

        playersMainTeam2.addAll(playersSubTeam2);

        Map<String,List<Player>> allTeamPlayer = new HashMap<>();
        allTeamPlayer.put("playersTeam1",playersMainTeam1);
        allTeamPlayer.put("playersTeam2",playersMainTeam2);

        return new ResponseEntity<>(allTeamPlayer, HttpStatus.OK);
    }

    private  void updateMatchInfo(Match_ match,Team teamWin,Team teamLose,int scoreWin,int scoreLose){
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if(match.getTeam1().getID() == teamWin.getID()){
            match.setTeam1Status("win");
            match.setScoreTeam1(scoreWin);
            match.setTeam2Status("lose");
            match.setScoreTeam2(scoreLose);
        }else{
            match.setTeam2Status("win");
            match.setScoreTeam2(scoreWin);
            match.setTeam1Status("lose");
            match.setScoreTeam1(scoreLose);
        }

        if(!match.getType().contains("group-stage")){
            if(match.getType().contains("playoff")){
                teamLose.setGroupStage("");
            }
            teamLose.setStop(0);
            teamService.createTeam(teamLose);

            if(match.getNextMatchIndex() != null){
                if(!pattern.matcher(match.getNextMatchIndex()).matches()){
                    teamWin.setStop(1);
                    teamWin.setGroupStage(match.getNextMatchIndex());
                    Team teamWin_ = teamService.createTeam(teamWin);
                    List<Match_> matchsInGroup = matchService.getMatchByIndexMatchForPlayOff(getTournamentCurrent().getID(),teamWin_.getGroupStage());
                    for(Match_ match_ : matchsInGroup){
                        if(match_.getTeam1() == null){
                            match_.setTeam1(teamWin_);
                        }else{
                            match_.setTeam2(teamWin_);
                        }
                    }
                    matchService.createAllMatch(matchsInGroup);
                    GroupStageInfo groupStageInfo = new GroupStageInfo();
                    groupStageInfo.setGroupTitle(teamWin_.getGroupStage().substring(0,1));
                    groupStageInfo.setTournament(getTournamentCurrent());
                    groupStageInfo.setTeam(teamWin_);
                    groupStageInfoService.create(groupStageInfo);
                }else if(!match.getType().equals("semi-final") && !match.getType().equals("final")){
                    Match_ nextMatch = matchService.getMatchByIndexMatch(getTournamentCurrent().getID(), match.getNextMatchIndex());
                    if(nextMatch.getTeam1() == null){
                        nextMatch.setTeam1(teamWin);
                    }else{
                        nextMatch.setTeam2(teamWin);
                    }
                    matchService.createMatch(nextMatch);
                }
            }
            else if (match.getType().equals("semi-final")) {
                Match_ nextMatchLose = matchService.getMatchByIndexMatch(getTournamentCurrent().getID(), "0");
                if(nextMatchLose.getTeam1() == null){
                    nextMatchLose.setTeam1(teamLose);
                }else{
                    nextMatchLose.setTeam2(teamLose);
                }

                Match_ nextMatchWin = matchService.getMatchByIndexMatch(getTournamentCurrent().getID(), "1");
                if(nextMatchWin.getTeam1() == null){
                    nextMatchWin.setTeam1(teamWin);
                }else{
                    nextMatchWin.setTeam2(teamWin);
                }
                matchService.createMatch(nextMatchLose);
                matchService.createMatch(nextMatchWin);
            }else if(match.getType().equals("third-place")){
                teamWin.setStatus("top3");
                teamService.createTeam(teamWin);
                teamService.createTeam(teamLose);
            }else if(match.getType().equals("final")){
                teamWin.setStatus("top1");
                teamLose.setStatus("top2");
                teamService.createTeam(teamWin);
                teamService.createTeam(teamLose);
            }
        }
        matchService.createMatch(match);
    }

    @PostMapping("/api/updateMatch")
    @PreAuthorize("hasAnyRole('MANAGER')")
    public ResponseEntity<?> updateMatch(@RequestBody DataModel dataModel){
        Integer matchID = dataModel.getMatchID();
        List<ScoreData> scoreTeam1 = dataModel.getScoreTeam1();
        List<ScoreData> scoreTeam2 = dataModel.getScoreTeam2();
        List<CardData> cardTeam1 = dataModel.getCardTeam1();
        List<CardData> cardTeam2 = dataModel.getCardTeam2();
        List<PenaltyData> penaltyTeam1 = dataModel.getPenaltyTeam1();
        List<PenaltyData> penaltyTeam2 = dataModel.getPenaltyTeam2();

        int redCardTeam1 = 0;
        int redCardTeam2 = 0;
        int yellowCardTeam1 = 0;
        int yellowCardTeam2 = 0;

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        // cập nhật trận đấu
        Match_ match = matchService.getMatchById(matchID);

        if(scoreTeam1.size() == scoreTeam2.size() && penaltyTeam1.size() == 0 && !match.getType().contains("group-stage")){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("");
        }

        // cầu thủ bị cấm cho phép đá lại
        List<Player> playersTeam1 = playerService.getPlayerByTeam(match.getTeam1().getID());
        List<Player> playersTeam2 = playerService.getPlayerByTeam(match.getTeam2().getID());

        for(Player player : playersTeam1){
            if(!player.getReasonBanned().isEmpty() || player.getReasonBanned() != null){
                player.setReasonBanned("");
            }
        }

        for(Player player : playersTeam2){
            if(!player.getReasonBanned().isEmpty() || player.getReasonBanned() != null){
                player.setReasonBanned("");
            }
        }

        playerService.createAllPlayer(playersTeam1);
        playerService.createAllPlayer(playersTeam2);

        if(match.getStatus().equals("playing")){
            match.setStatus("done");
            if(scoreTeam1.size() > scoreTeam2.size()){
                updateMatchInfo(match,match.getTeam1(),match.getTeam2(),scoreTeam1.size(),scoreTeam2.size());
            }else if(scoreTeam1.size() < scoreTeam2.size()){
                updateMatchInfo(match,match.getTeam2(),match.getTeam1(),scoreTeam2.size(),scoreTeam1.size());
            }else{
                if(match.getType().contains("group-stage")){
                    match.setTeam2Status("draw");
                    match.setScoreTeam1(scoreTeam1.size());
                    match.setTeam1Status("draw");
                    match.setScoreTeam2(scoreTeam2.size());
                }else{
                    if(penaltyTeam1.size() > 0 && penaltyTeam2.size() > 0){
                        match.setPenaltyCheck(1);
                        int resultPenaltyTeam1 = 0;
                        int resultPenaltyTeam2 = 0;
                        livePenaltyService.deleteByMatchIDAndTeamID(matchID,match.getTeam1().getID());
                        livePenaltyService.deleteByMatchIDAndTeamID(matchID,match.getTeam2().getID());
                        List<LivePenalty> livePenalties = new ArrayList<>();
                        for(PenaltyData penalty : penaltyTeam1){
                            if(penalty.getPlayerPenalty() != null && penalty.getResultPenalty() != null){
                                LivePenalty livePenalty = new LivePenalty();
                                livePenalty.setMatch(match);
                                livePenalty.setTeam(match.getTeam1());
                                Player player = playerService.getPlayerByID(penalty.getPlayerPenalty());
                                livePenalty.setPlayer(player);
                                livePenalty.setResult(penalty.getResultPenalty());
                                livePenalties.add(livePenalty);
                                if(penalty.getResultPenalty() == 1){
                                    resultPenaltyTeam1 += 1;
                                }
                            }
                        }
                        for(PenaltyData penalty : penaltyTeam2){
                            if(penalty.getPlayerPenalty() != null && penalty.getResultPenalty() != null){
                                LivePenalty livePenalty = new LivePenalty();
                                livePenalty.setMatch(match);
                                livePenalty.setTeam(match.getTeam2());
                                Player player = playerService.getPlayerByID(penalty.getPlayerPenalty());
                                livePenalty.setPlayer(player);
                                livePenalty.setResult(penalty.getResultPenalty());
                                livePenalties.add(livePenalty);
                                if(penalty.getResultPenalty() == 1){
                                    resultPenaltyTeam2 += 1;
                                }
                            }
                        }
                        livePenaltyService.createAll(livePenalties);

                        if(resultPenaltyTeam1 > resultPenaltyTeam2){
                            updateMatchInfo(match,match.getTeam1(),match.getTeam2(),scoreTeam1.size(),scoreTeam2.size());
                        }else if(resultPenaltyTeam1 < resultPenaltyTeam2){
                            updateMatchInfo(match,match.getTeam2(),match.getTeam1(),scoreTeam2.size(),scoreTeam1.size());
                        }
                        match.setPenaltyScoreTeam1(resultPenaltyTeam1);
                        match.setPenaltyScoreTeam2(resultPenaltyTeam2);
                    }
                }
            }

            // cập nhật kết quả
            List<Livescore> livescores = new ArrayList<>();
            if(scoreTeam1.size() > 0){
                livescoreService.deleteByMatchIDAndTeamID(matchID,match.getTeam1().getID());
                for(ScoreData score : scoreTeam1){
                    if(score.getPlayerScore() != null && score.getMinutesScore() != null){
                        Livescore livescore = new Livescore();
                        Player playerScore = playerService.getPlayerByID(score.getPlayerScore());
                        livescore.setPlayerScore(playerScore);
                        livescore.setTeam(match.getTeam1());
                        livescore.setMatch(match);
                        if(score.getPlayerAssist() != null){
                            Player playerAssist = playerService.getPlayerByID(score.getPlayerAssist());
                            livescore.setPlayerAssist(playerAssist);
                            int playerAssist_ = playerAssist.getAssists() + 1;
                            playerAssist.setAssists(playerAssist_);
                            playerService.createPlayer(playerAssist);
                        }
                        livescore.setMinutes(score.getMinutesScore());
                        if(playerScore.getTeam().getID() == match.getTeam1().getID()){
                            int playerGoals = playerScore.getGoal() + 1;
                            playerScore.setGoal(playerGoals);
                            playerService.createPlayer(playerScore);
                            livescore.setType("Goal");
                        }else{
                            livescore.setType("OwnGoal");
                        }
                        livescores.add(livescore);
                    }
                }
            }
            if(scoreTeam2.size() > 0){
                livescoreService.deleteByMatchIDAndTeamID(matchID,match.getTeam2().getID());
                for(ScoreData score : scoreTeam2){
                    if(score.getPlayerScore() != null && score.getMinutesScore() != null){
                        Livescore livescore = new Livescore();
                        Player playerScore = playerService.getPlayerByID(score.getPlayerScore());
                        livescore.setPlayerScore(playerScore);
                        livescore.setTeam(match.getTeam2());
                        livescore.setMatch(match);
                        if(score.getPlayerAssist() != null){
                            Player playerAssist = playerService.getPlayerByID(score.getPlayerAssist());
                            livescore.setPlayerAssist(playerAssist);
                            int playerAssist_ = playerAssist.getAssists() + 1;
                            playerAssist.setAssists(playerAssist_);
                            playerService.createPlayer(playerAssist);
                        }
                        livescore.setMinutes(score.getMinutesScore());
                        if(playerScore.getTeam().getID() == match.getTeam2().getID()){
                            livescore.setType("Goal");
                            int playerGoals = playerScore.getGoal() + 1;
                            playerScore.setGoal(playerGoals);
                            playerService.createPlayer(playerScore);
                        }else{
                            livescore.setType("OwnGoal");
                        }
                        livescores.add(livescore);
                    }

                }
            }

            if(livescores.size() > 0){
                livescoreService.createAll(livescores);
            }
            // cập nhật thẻ
            Map<Integer,Integer> checkCardPlayer = new HashMap<>();
            liveCardService.deleteByMatchIDAndTeamID(matchID,match.getTeam1().getID());
            liveCardService.deleteByMatchIDAndTeamID(matchID,match.getTeam2().getID());
            List<LiveCard> liveCards = new ArrayList<>();
            if(cardTeam1.size() > 0){
                for (CardData card : cardTeam1){
                    if(card.getPlayerCard() != null && card.getMinutesCard() != null && card.getTypeCard() != null){
                        LiveCard liveCard = new LiveCard();
                        liveCard.setMatch(match);
                        liveCard.setTeam(match.getTeam1());
                        liveCard.setMinutes(card.getMinutesCard());
                        Player player = playerService.getPlayerByID(card.getPlayerCard());
                        if(card.getTypeCard().equals("redCard")){
                            liveCard.setType("redCard");
                            player.setRedCard(player.getRedCard()+1);
                            player.setBannedNextMatch(1);
                            player.setReasonBanned("redCard");
                            redCardTeam1 += 1;
                        }else{
                            if(!checkCardPlayer.containsKey(card.getPlayerCard())){
                                liveCard.setType("yellowCard");
                                player.setYellowCard(player.getYellowCard()+1);
                                player.setBannedNextMatch(0);
                                player.setReasonBanned("yellowCard");
                                checkCardPlayer.put(card.getPlayerCard(),1);
                            }else{
                                liveCard.setType("yellowCard");
                                player.setYellowCard(player.getYellowCard()+1);
                                player.setBannedNextMatch(1);
                                player.setReasonBanned("doubleYellowCard");
                            }
                            yellowCardTeam1 += 1;
                        }
                        playerService.createPlayer(player);
                        liveCard.setPlayer(player);
                        liveCards.add(liveCard);
                    }
                }
            }
            if(cardTeam2.size() > 0){
                for (CardData card : cardTeam2){
                    if (card.getPlayerCard() != null && card.getMinutesCard() != null && card.getTypeCard() != null) {
                        LiveCard liveCard = new LiveCard();
                        liveCard.setMatch(match);
                        liveCard.setTeam(match.getTeam2());
                        liveCard.setMinutes(card.getMinutesCard());
                        Player player = playerService.getPlayerByID(card.getPlayerCard());
                        if (card.getTypeCard().equals("redCard")) {
                            player.setRedCard(player.getRedCard()+1);
                            player.setBannedNextMatch(1);
                            player.setReasonBanned("redCard");
                            liveCard.setType("redCard");
                            redCardTeam2 += 1;
                        } else {
                            if (!checkCardPlayer.containsKey(card.getPlayerCard())) {
                                player.setBannedNextMatch(0);
                                player.setReasonBanned("yellowCard");
                                player.setYellowCard(player.getYellowCard()+1);
                                checkCardPlayer.put(card.getPlayerCard(), 1);
                                liveCard.setType("yellowCard");
                            } else {
                                liveCard.setType("yellowCard");
                                player.setBannedNextMatch(1);
                                player.setReasonBanned("doubleYellowCard");
                                player.setYellowCard(player.getYellowCard()+1);
                            }
                            yellowCardTeam2 += 1;
                        }
                        playerService.createPlayer(player);
                        liveCard.setPlayer(player);
                        liveCards.add(liveCard);
                    }
                }
            }
            if(liveCards.size() > 0){
                match.setRedCardTeam1(redCardTeam1);
                match.setRedCardTeam2(redCardTeam2);
                match.setYellowCardTeam1(yellowCardTeam1);
                match.setYellowCardTeam2(yellowCardTeam2);
                System.out.println("cập nhật create");
                liveCardService.createAll(liveCards);
            }

            // cho phép thi đấu trận sau những cầu thủ bị cấm trận này (thẻ đỏ, 2 thẻ vàng)
            List<Attendance> bannedTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(matchID,match.getTeam1().getID(),"banned");
            List<Player> playersBannedTeam1 = bannedTeam1.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            for(Player player: playersBannedTeam1){
                player.setBannedNextMatch(0);
            }
            playerService.createAllPlayer(playersBannedTeam1);

            List<Attendance> bannedTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(matchID,match.getTeam2().getID(),"banned");
            List<Player> playersBannedTeam2 = bannedTeam2.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            for(Player player: playersBannedTeam2){
                player.setBannedNextMatch(0);
            }
            playerService.createAllPlayer(playersBannedTeam2);

            // lấy thông tin vòng đấu
            RoundSchedule roundSchedule;
            if(match.getType().contains("group-stage")){
                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(), "group-stage");
            }else{
                if(match.getType().equals("third-place")){
                    roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(), "final");
                }else{
                    roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(), match.getType());
                }
            }

            // kiểm tra vòng đấu tiếp theo
            int matchDone = roundSchedule.getNumberMatchDone() + 1;
            roundSchedule.setNumberMatchDone(matchDone);
            if(matchDone == roundSchedule.getNumberMatch() &&  !roundSchedule.getRoundTitle().equals("final")){
                roundSchedule.setScheduleStatus("done");
                int roundNextScheduleID = roundSchedule.getScheduleID() - 1;
                RoundSchedule  roundNextSchedule = roundScheduleService.getRoundScheduleByID(roundNextScheduleID);
                roundNextSchedule.setScheduleStatus("inprogress");
                roundNextSchedule = roundScheduleService.createRoundSchedule(roundNextSchedule);

                // chuyển các trận ở vòng tiếp theo qua trạng thái diễn ra
                if(roundNextSchedule.getRoundTitle().contains("group-stage")){
                    int numberGroup = 0;
                    if(roundNextSchedule.getNumberMatch() == 48 || roundNextSchedule.getNumberMatch() == 24){
                        numberGroup = 8;
                    }else{
                        numberGroup = 7;
                    }
                    for (int i = 0; i < numberGroup; i++) {
                        char letter = (char) ('A' + i);
                        List<Match_> matchsInRound = matchService.getMatchByType(getTournamentCurrent().getID(), "group-stage-"+String.valueOf(letter));
                        for (Match_ matchInRound : matchsInRound){
                            matchInRound.setStatus("ingame");
                        }
                        matchService.createAllMatch(matchsInRound);
                    }
                }else if(!roundNextSchedule.getRoundTitle().contains("final")){
                    List<Match_> matchsInRound = matchService.getMatchByType(getTournamentCurrent().getID(), roundNextSchedule.getRoundTitle());
                    for (Match_ matchInRound : matchsInRound){
                        matchInRound.setStatus("ingame");
                    }
                    matchService.createAllMatch(matchsInRound);
                }else{
                    List<Match_> matchsInRound = matchService.getMatchByType(getTournamentCurrent().getID(), "final");
                    matchsInRound.addAll(matchService.getMatchByType(getTournamentCurrent().getID(), "third-place"));
                    for (Match_ matchInRound : matchsInRound){
                        matchInRound.setStatus("ingame");
                    }
                    matchService.createAllMatch(matchsInRound);
                }

                if(roundSchedule.getRoundTitle().equals("group-stage")){
                    // hết vòng bảng chọn đội vào 1/16
                    if(roundSchedule.getNumberMatch() == 48  || roundSchedule.getNumberMatch() == 24){
                        // lấy 2 top đầu mỗi bảng
                        List<GroupStageInfo> rank1Teams = groupStageInfoService.getByTournamentAndRank(getTournamentCurrent().getID(), 0);
                        List<GroupStageInfo> rank2Teams = groupStageInfoService.getByTournamentAndRank(getTournamentCurrent().getID(), 1);
                        List<Match_> matchsNextRound = matchService.getMatchByType(getTournamentCurrent().getID(), roundNextSchedule.getRoundTitle());
                        Random random = new Random();
                        for(Match_ match_ : matchsNextRound){
                            int rank1TeamIndex = random.nextInt(rank1Teams.size());
                            int rank2TeamIndex = random.nextInt(rank2Teams.size());
                            while (rank1Teams.get(rank1TeamIndex).getGroupTitle().equals(rank2Teams.get(rank2TeamIndex).getGroupTitle())){
                                rank1TeamIndex = random.nextInt(rank1Teams.size());
                                rank2TeamIndex = random.nextInt(rank2Teams.size());
                            }
                            GroupStageInfo rank1TeamGroup = rank1Teams.remove(rank1TeamIndex);
                            GroupStageInfo rank2TeamGroup = rank2Teams.remove(rank2TeamIndex);
                            match_.setTeam1(rank1TeamGroup.getTeam());
                            match_.setTeam2(rank2TeamGroup.getTeam());
                            match_.setStatus("ingame");
                            matchService.createMatch(match_);
                        }

                        // đội top 3 4 dừng chân
                        List<Integer> ranksIndex = new ArrayList<>();
                        ranksIndex.add(0);
                        ranksIndex.add(1);
                        List<GroupStageInfo> rankOutTeams = groupStageInfoService.getByTournamentAndExcludeRanks(getTournamentCurrent().getID(), ranksIndex);
                        for(GroupStageInfo groupStageInfo: rankOutTeams){
                            Team team = groupStageInfo.getTeam();
                            team.setStop(0);
                            teamService.createTeam(team);
                        }
                    } else if (roundSchedule.getNumberMatch() == 42) {
                        // lấy 2 top đầu mỗi bảng + 2 đội hiệu số tốt
                        List<GroupStageInfo> rank1Teams = groupStageInfoService.getByTournamentAndRank(getTournamentCurrent().getID(), 0);
                        List<GroupStageInfo> rank2Teams = groupStageInfoService.getByTournamentAndRank(getTournamentCurrent().getID(), 1);
                        List<GroupStageInfo> rankBestThirdTeams = groupStageInfoService.getRankingsBySortedRanks(getTournamentCurrent().getID(), 2);
                        rankBestThirdTeams.subList(2,rankBestThirdTeams.size()).clear();
                        List<Team> teamsBestThird = rankBestThirdTeams.stream().map(GroupStageInfo::getTeam).toList();
                        rank1Teams.add(rankBestThirdTeams.get(0));
                        rank2Teams.add(rankBestThirdTeams.get(1));
                        List<Match_> matchsNextRound = matchService.getMatchByType(getTournamentCurrent().getID(), roundNextSchedule.getRoundTitle());
                        Random random = new Random();
                        for(Match_ match_ : matchsNextRound){
                            int rank1TeamIndex = random.nextInt(rank1Teams.size());
                            int rank2TeamIndex = random.nextInt(rank2Teams.size());
                            while (rank1Teams.get(rank1TeamIndex).getGroupTitle().equals(rank2Teams.get(rank2TeamIndex).getGroupTitle())){
                                rank1TeamIndex = random.nextInt(rank1Teams.size());
                                rank2TeamIndex = random.nextInt(rank2Teams.size());
                            }
                            GroupStageInfo rank1TeamGroup = rank1Teams.remove(rank1TeamIndex);
                            GroupStageInfo rank2TeamGroup = rank2Teams.remove(rank2TeamIndex);
                            match_.setTeam1(rank1TeamGroup.getTeam());
                            match_.setTeam2(rank2TeamGroup.getTeam());
                            match_.setStatus("ingame");
                            matchService.createMatch(match_);
                        }
                        List<Integer> ranksIndex = new ArrayList<>();
                        ranksIndex.add(0);
                        ranksIndex.add(1);
                        List<GroupStageInfo> rankOutTeams = groupStageInfoService.getByTournamentAndExcludeRanks(getTournamentCurrent().getID(), ranksIndex);
                        for(GroupStageInfo groupStageInfo: rankOutTeams){
                            Team team = groupStageInfo.getTeam();
                            if(!teamsBestThird.contains(team)){
                                team.setStop(0);
                                teamService.createTeam(team);
                            }
                        }
                    }
                }
            }else if(matchDone == roundSchedule.getNumberMatch() && roundSchedule.getRoundTitle().equals("final")){
                roundSchedule.setScheduleStatus("done");
                Tournament tournament = getTournamentCurrent();
                tournament.setStatus("close");
                tournamentService.createTournament(tournament);
                SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
                schoolYear.setCurrent("0");
                schoolYearService.createSchoolYear(schoolYear);
            }
            roundScheduleService.createRoundSchedule(roundSchedule);
            matchService.createMatch(match);

            // xử lý đội vòng bảng (tính hiệu suất)
            if(match.getType().contains("group-stage")){
                String groupTitle = match.getType().split("-")[2];
                String team1Status = match.getTeam1Status();
                Team team1 = match.getTeam1();
                String team2Status = match.getTeam2Status();
                Team team2 = match.getTeam2();
                GroupStageInfo groupTeam1 = groupStageInfoService.getByTournamentAndTeam(getTournamentCurrent().getID(), team1.getID());
                GroupStageInfo groupTeam2 = groupStageInfoService.getByTournamentAndTeam(getTournamentCurrent().getID(), team2.getID());
                if(team1Status.equals("win")){

                    int team1GoalsScored = (groupTeam1.getGoalsScored() != null ? groupTeam1.getGoalsScored() : 0) + (match.getScoreTeam1() != null ? match.getScoreTeam1() : 0);
                    int team1GoalsAgainst = (groupTeam1.getGoalsAgainst() != null ? groupTeam1.getGoalsAgainst() : 0) + (match.getScoreTeam2() != null ? match.getScoreTeam2() : 0);
                    int team1GoalsDifference = team1GoalsScored - team1GoalsAgainst;
                    int team1Win = (groupTeam1.getWin() != null ? groupTeam1.getWin() : 0) + 1;
                    int team1Lose = (groupTeam1.getLose() != null ? groupTeam1.getLose() : 0);
                    int team1Draw = (groupTeam1.getDraw() != null ? groupTeam1.getDraw() : 0);
                    int team1RedCard = (groupTeam1.getRedCard() != null ? groupTeam1.getRedCard() : 0) + redCardTeam1;
                    int team1YellowCard = (groupTeam1.getYellowCard() != null ? groupTeam1.getYellowCard() : 0) + yellowCardTeam1;
                    int team1Point = (groupTeam1.getPoint() != null ? groupTeam1.getPoint() : 0) + 3;
                    int team1CountMatch = (groupTeam1.getTotalMatch() != null ? groupTeam1.getTotalMatch() : 0) + 1;
                    String team1LastMatch = (groupTeam1.getLastMatch() != null ? groupTeam1.getLastMatch() : "") + "-W-";

                    groupTeam1.setGoalsScored(team1GoalsScored);
                    groupTeam1.setGoalsAgainst(team1GoalsAgainst);
                    groupTeam1.setGoalsDifference(team1GoalsDifference);
                    groupTeam1.setWin(team1Win);
                    groupTeam1.setLose(team1Lose);
                    groupTeam1.setDraw(team1Draw);
                    groupTeam1.setYellowCard(team1YellowCard);
                    groupTeam1.setRedCard(team1RedCard);
                    groupTeam1.setPoint(team1Point);
                    groupTeam1.setTotalMatch(team1CountMatch);
                    groupTeam1.setLastMatch(team1LastMatch);

                    int team2GoalsScored = (groupTeam2.getGoalsScored() != null ? groupTeam2.getGoalsScored() : 0) + (match.getScoreTeam2() != null ? match.getScoreTeam2() : 0);
                    int team2GoalsAgainst = (groupTeam2.getGoalsAgainst() != null ? groupTeam2.getGoalsAgainst() : 0) + (match.getScoreTeam1() != null ? match.getScoreTeam1() : 0);
                    int team2GoalsDifference = team2GoalsScored - team2GoalsAgainst;
                    int team2Win = (groupTeam2.getWin() != null ? groupTeam2.getWin() : 0);
                    int team2Lose = (groupTeam2.getLose() != null ? groupTeam2.getLose() : 0) + 1;
                    int team2Draw = (groupTeam2.getDraw() != null ? groupTeam2.getDraw() : 0);
                    int team2RedCard = (groupTeam2.getRedCard() != null ? groupTeam2.getRedCard() : 0) + redCardTeam2;
                    int team2YellowCard = (groupTeam2.getYellowCard() != null ? groupTeam2.getYellowCard() : 0) + yellowCardTeam2;
                    int team2CountMatch = (groupTeam2.getTotalMatch() != null ? groupTeam2.getTotalMatch() : 0) + 1;
                    int team2Point = (groupTeam2.getPoint() != null ? groupTeam2.getPoint() : 0);
                    String team2LastMatch = (groupTeam2.getLastMatch() != null ? groupTeam2.getLastMatch() : "") + "-L-";

                    groupTeam2.setGoalsScored(team2GoalsScored);
                    groupTeam2.setGoalsAgainst(team2GoalsAgainst);
                    groupTeam2.setGoalsDifference(team2GoalsDifference);
                    groupTeam2.setLose(team2Lose);
                    groupTeam2.setDraw(team2Draw);
                    groupTeam2.setWin(team2Win);
                    groupTeam2.setYellowCard(team2YellowCard);
                    groupTeam2.setRedCard(team2RedCard);
                    groupTeam2.setTotalMatch(team2CountMatch);
                    groupTeam2.setLastMatch(team2LastMatch);
                    groupTeam2.setPoint(team2Point);
                } else if (team2Status.equals("win")) {

                    int team2GoalsScored = (groupTeam2.getGoalsScored() != null ? groupTeam2.getGoalsScored() : 0)
                            + (match.getScoreTeam2() != null ? match.getScoreTeam2() : 0);
                    int team2GoalsAgainst = (groupTeam2.getGoalsAgainst() != null ? groupTeam2.getGoalsAgainst() : 0)
                            + (match.getScoreTeam1() != null ? match.getScoreTeam1() : 0);
                    int team2GoalsDifference = team2GoalsScored - team2GoalsAgainst;
                    int team2Win = (groupTeam2.getWin() != null ? groupTeam2.getWin() : 0) + 1;
                    int team2Lose = (groupTeam2.getLose() != null ? groupTeam2.getLose() : 0);
                    int team2Draw = (groupTeam2.getDraw() != null ? groupTeam2.getDraw() : 0);
                    int team2RedCard = (groupTeam2.getRedCard() != null ? groupTeam2.getRedCard() : 0) + redCardTeam2 ;
                    int team2YellowCard = (groupTeam2.getYellowCard() != null ? groupTeam2.getYellowCard() : 0) + yellowCardTeam2;
                    int team2Point = (groupTeam2.getPoint() != null ? groupTeam2.getPoint() : 0) + 3;
                    int team2CountMatch = (groupTeam2.getTotalMatch() != null ? groupTeam2.getTotalMatch() : 0) + 1;
                    String team2LastMatch = (groupTeam2.getLastMatch() != null ? groupTeam2.getLastMatch() : "") + "-W-";


                    groupTeam2.setGoalsScored(team2GoalsScored);
                    groupTeam2.setGoalsAgainst(team2GoalsAgainst);
                    groupTeam2.setGoalsDifference(team2GoalsDifference);
                    groupTeam2.setWin(team2Win);
                    groupTeam2.setLose(team2Lose);
                    groupTeam2.setDraw(team2Draw);
                    groupTeam2.setYellowCard(team2YellowCard);
                    groupTeam2.setRedCard(team2RedCard);
                    groupTeam2.setPoint(team2Point);
                    groupTeam2.setTotalMatch(team2CountMatch);
                    groupTeam2.setLastMatch(team2LastMatch);
                    groupTeam2.setTotalMatch(team2CountMatch);
                    groupTeam2.setLastMatch(team2LastMatch);

                    int team1GoalsScored = (groupTeam1.getGoalsScored() != null ? groupTeam1.getGoalsScored() : 0)
                            + (match.getScoreTeam1() != null ? match.getScoreTeam1() : 0);
                    int team1GoalsAgainst = (groupTeam1.getGoalsAgainst() != null ? groupTeam1.getGoalsAgainst() : 0)
                            + (match.getScoreTeam2() != null ? match.getScoreTeam2() : 0);
                    int team1GoalsDifference = team1GoalsScored - team1GoalsAgainst;
                    int team1Lose = (groupTeam1.getLose() != null ? groupTeam1.getLose() : 0) + 1;
                    int team1Win = (groupTeam1.getWin() != null ? groupTeam1.getWin() : 0);
                    int team1Draw = (groupTeam1.getDraw() != null ? groupTeam1.getDraw() : 0);
                    int team1RedCard = (groupTeam1.getRedCard() != null ? groupTeam1.getRedCard() : 0)
                            + redCardTeam1;
                    int team1YellowCard = (groupTeam1.getYellowCard() != null ? groupTeam1.getYellowCard() : 0)
                            + yellowCardTeam1;
                    int team1CountMatch = (groupTeam1.getTotalMatch() != null ? groupTeam1.getTotalMatch() : 0) + 1;
                    int team1Point = (groupTeam1.getPoint() != null ? groupTeam1.getPoint() : 0);
                    String team1LastMatch = (groupTeam1.getLastMatch() != null ? groupTeam1.getLastMatch() : "") + "-L-";

                    groupTeam1.setGoalsScored(team1GoalsScored);
                    groupTeam1.setGoalsAgainst(team1GoalsAgainst);
                    groupTeam1.setGoalsDifference(team1GoalsDifference);
                    groupTeam1.setLose(team1Lose);
                    groupTeam1.setDraw(team1Draw);
                    groupTeam1.setWin(team1Win);
                    groupTeam1.setYellowCard(team1YellowCard);
                    groupTeam1.setRedCard(team1RedCard);
                    groupTeam1.setTotalMatch(team1CountMatch);
                    groupTeam1.setLastMatch(team1LastMatch);
                    groupTeam1.setPoint(team1Point);

                }else if (team2Status.equals("draw")) {

                    int team1GoalsScored = (groupTeam1.getGoalsScored() != null ? groupTeam1.getGoalsScored() : 0)
                            + (match.getScoreTeam1() != null ? match.getScoreTeam1() : 0);
                    int team1GoalsAgainst = (groupTeam1.getGoalsAgainst() != null ? groupTeam1.getGoalsAgainst() : 0)
                            + (match.getScoreTeam2() != null ? match.getScoreTeam2() : 0);
                    int team1GoalsDifference = team1GoalsScored - team1GoalsAgainst;
                    int team1Draw = (groupTeam1.getDraw() != null ? groupTeam1.getDraw() : 0) + 1;
                    int team1Win = (groupTeam1.getWin() != null ? groupTeam1.getWin() : 0) ;
                    int team1Lose = (groupTeam1.getLose() != null ? groupTeam1.getLose() : 0);
                    int team1RedCard = (groupTeam1.getRedCard() != null ? groupTeam1.getRedCard() : 0)
                            + redCardTeam1;
                    int team1YellowCard = (groupTeam1.getYellowCard() != null ? groupTeam1.getYellowCard() : 0)
                            + yellowCardTeam1;
                    int team1Point = (groupTeam1.getPoint() != null ? groupTeam1.getPoint() : 0) + 1;
                    int team1CountMatch = (groupTeam1.getTotalMatch() != null ? groupTeam1.getTotalMatch() : 0) + 1;
                    String team1LastMatch = (groupTeam1.getLastMatch() != null ? groupTeam1.getLastMatch() : "") + "-D-";

                    groupTeam1.setGoalsScored(team1GoalsScored);
                    groupTeam1.setGoalsAgainst(team1GoalsAgainst);
                    groupTeam1.setGoalsDifference(team1GoalsDifference);
                    groupTeam1.setDraw(team1Draw);
                    groupTeam1.setWin(team1Win);
                    groupTeam1.setLose(team1Lose);
                    groupTeam1.setYellowCard(team1YellowCard);
                    groupTeam1.setRedCard(team1RedCard);
                    groupTeam1.setPoint(team1Point);
                    groupTeam1.setTotalMatch(team1CountMatch);
                    groupTeam1.setLastMatch(team1LastMatch);

                    int team2GoalsScored = (groupTeam2.getGoalsScored() != null ? groupTeam2.getGoalsScored() : 0)
                            + (match.getScoreTeam2() != null ? match.getScoreTeam2() : 0);
                    int team2GoalsAgainst = (groupTeam2.getGoalsAgainst() != null ? groupTeam2.getGoalsAgainst() : 0)
                            + (match.getScoreTeam1() != null ? match.getScoreTeam1() : 0);
                    int team2GoalsDifference = team2GoalsScored - team2GoalsAgainst;

                    int team2Draw = (groupTeam2.getDraw() != null ? groupTeam2.getDraw() : 0) + 1;
                    int team2Win = (groupTeam2.getDraw() != null ? groupTeam2.getDraw() : 0) ;
                    int team2Lose = (groupTeam2.getDraw() != null ? groupTeam2.getDraw() : 0);
                    int team2RedCard = (groupTeam2.getRedCard() != null ? groupTeam2.getRedCard() : 0)
                            + redCardTeam2;
                    int team2YellowCard = (groupTeam2.getYellowCard() != null ? groupTeam2.getYellowCard() : 0)
                            + yellowCardTeam2;

                    int team2Point = (groupTeam2.getPoint() != null ? groupTeam2.getPoint() : 0) + 1;
                    int team2CountMatch = (groupTeam2.getTotalMatch() != null ? groupTeam2.getTotalMatch() : 0) + 1;

                    String team2LastMatch = (groupTeam2.getLastMatch() != null ? groupTeam2.getLastMatch() : "") + "-D-";


                    groupTeam2.setGoalsScored(team2GoalsScored);
                    groupTeam2.setGoalsAgainst(team2GoalsAgainst);
                    groupTeam2.setGoalsDifference(team2GoalsDifference);
                    groupTeam2.setDraw(team2Draw);
                    groupTeam2.setWin(team2Win);
                    groupTeam2.setLose(team2Lose);
                    groupTeam2.setYellowCard(team2YellowCard);
                    groupTeam2.setRedCard(team2RedCard);
                    groupTeam2.setPoint(team2Point);
                    groupTeam2.setTotalMatch(team2CountMatch);
                    groupTeam2.setLastMatch(team2LastMatch);
                }
                groupStageInfoService.create(groupTeam1);
                groupStageInfoService.create(groupTeam2);

                // xếp hạng lại đội trong group
                int rank = 0;
                List<GroupStageInfo> groupStageInfos = groupStageInfoService.getRankingsByGroupTitle(getTournamentCurrent().getID(), groupTitle);
                for(GroupStageInfo groupStageInfo : groupStageInfos){
                    groupStageInfo.setRank(rank);
                    rank += 1;
                }
                groupStageInfoService.createAll(groupStageInfos);
            }
        }

        return ResponseEntity.status(HttpStatus.OK).body("");
    }

//    @PostMapping("/api/cancelMatch")
//    @PreAuthorize("hasAnyRole('MANAGER')")
//    public ResponseEntity<?> cancelMatch(@RequestBody   Map<String, String> data){
//        String status = data.get("status");
//        String matchID = data.get("matchID");
//        int matchIDTrans = -1;
//        if(status != null || matchID != null){
//            Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
//            if(pattern.matcher(matchID).matches()){
//                matchIDTrans = Integer.parseInt(matchID);
//            }else {
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("");
//            }
//            if(matchIDTrans < 0){
//                return ResponseEntity.status(HttpStatus.CONFLICT).body("");
//            }
//        }
//
//
//
//        Match_ match = matchService.getMatchById(matchIDTrans);
//        if(match != null){
//            RoundSchedule roundSchedule = null;
//            RoundSchedule nextRoundSchedule = null;
//            if(match.getType().equals("group-stage")){
//                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(), "group-stage");
//                nextRoundSchedule = roundScheduleService.getRoundScheduleByID(roundSchedule.getScheduleID());
//            } else{
//
//                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(), match.getType());
//                nextRoundSchedule = roundScheduleService.getRoundScheduleByID(roundSchedule.getScheduleID());
//            }
//
//
//            if(status.equals("team1")){
//                match.setReasonTeam2("Xử thua");
//                match.setTeam1Status("freeWin");
//                match.setTeam2Status("lose");
//                match.setStatus("cancel");
//                match.setScoreTeam1(3);
//                match.setScoreTeam2(0);
//
//                if(roundSchedule.getRoundTitle().equals("group-stage")){
//                    GroupStageInfo teamWin = groupStageInfoService.getByTournamentAndTeam(getTournamentCurrent().getID(), match.getTeam1().getID());
//                    GroupStageInfo teamLose = groupStageInfoService.getByTournamentAndTeam(getTournamentCurrent().getID(), match.getTeam2().getID());
//                    int teamWinPoint = (teamWin.getPoint() != null ? teamWin.getPoint() : 0) + 3;
//                    teamWin.setPoint(teamWinPoint);
//                    int teamWinPlus = (teamWin.getWin() != null ? teamWin.getWin() : 0) + 1;
//                    teamWin.setWin(teamWinPlus);
//                    int teamLosePlus = (teamLose.getLose() != null ? teamLose.getLose() : 0) + 1;
//                    teamLose.setLose(teamLosePlus);
//                }
//            }else if (status.equals("team2")){
//                match.setReasonTeam1("Xử thua");
//                match.setTeam2Status("freeWin");
//                match.setTeam1Status("lose");
//                match.setStatus("cancel");
//                match.setScoreTeam2(3);
//                match.setScoreTeam1(0);
//                if(roundSchedule.getRoundTitle().equals("group-stage")){
//                    GroupStageInfo teamWin = groupStageInfoService.getByTournamentAndTeam(getTournamentCurrent().getID(), match.getTeam2().getID());
//                    GroupStageInfo teamLose = groupStageInfoService.getByTournamentAndTeam(getTournamentCurrent().getID(), match.getTeam1().getID());
//                    int teamWinPoint = (teamWin.getPoint() != null ? teamWin.getPoint() : 0) + 3;
//                    teamWin.setPoint(teamWinPoint);
//                    int teamWinPlus = (teamWin.getWin() != null ? teamWin.getWin() : 0) + 1;
//                    teamWin.setWin(teamWinPlus);
//                    int teamLosePlus = (teamLose.getLose() != null ? teamLose.getLose() : 0) + 1;
//                    teamLose.setLose(teamLosePlus);
//                }
//            }else{
//                match.setReasonTeam1("Xử thua");
//                match.setReasonTeam2("Xử thua");
//                match.setTeam2Status("lose");
//                match.setTeam1Status("lose");
//                match.setStatus("cancel");
//                match.setScoreTeam2(0);
//                match.setScoreTeam1(0);
//                if(roundSchedule.getRoundTitle().equals("group-stage")){
//                    GroupStageInfo team1 = groupStageInfoService.getByTournamentAndTeam(getTournamentCurrent().getID(), match.getTeam1().getID());
//                    GroupStageInfo team2 = groupStageInfoService.getByTournamentAndTeam(getTournamentCurrent().getID(), match.getTeam2().getID());
//                    int teamLose1 = (team1.getLose() != null ? team1.getLose() : 0) + 1;
//                    team1.setWin(teamLose1);
//                    int teamLose2 = (team2.getLose() != null ? team2.getLose() : 0) + 1;
//                    team2.setLose(teamLose2);
//                }
//            }
//
//
//        }
//
//        return ResponseEntity.status(HttpStatus.OK).body("");
//    }

}
