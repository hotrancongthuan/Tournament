package com.duan2.tournamentTDTU.controller;

import com.duan2.tournamentTDTU.draw.DrawMatchService;
import com.duan2.tournamentTDTU.fileUpload.AvatarUpload;
import com.duan2.tournamentTDTU.fileUpload.FileService;
import com.duan2.tournamentTDTU.fileUpload.ProcessFileService;
import com.duan2.tournamentTDTU.fileUpload.UploadProcessService;
import com.duan2.tournamentTDTU.genetic_algorithm.GeneticAlgorithmService;
import com.duan2.tournamentTDTU.genetic_algorithm.Test;
import com.duan2.tournamentTDTU.models.*;
import com.duan2.tournamentTDTU.services.*;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;


@org.springframework.stereotype.Controller
public class Controller
{

    @Autowired
    private AccountService accountService;

    @Autowired
    private UserService userService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TakeInService takeInService;

    @Autowired
    private SchoolYearService schoolYearService;

    @Autowired
    private TournamentTakeInService tournamentTakeInService;

    @Autowired
    private SemesterWeekService semesterWeekService;

    @Autowired
    private FileService fileService;

    @Autowired
    private ProcessFileService processFileService;

    @Autowired
    private UploadProcessService uploadProcessService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private ScheduleSubjectService scheduleSubjectService;

    @Autowired
    private ScheduleStudentService scheduleStudentService;

    @Autowired
    private PlanYearService planYearService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AvatarUpload avatarUpload;

    @Autowired
    private Test test;

    @Autowired
    private DrawMatchService drawMatchService;

    @Autowired
    private RoundScheduleService roundScheduleService;

    @Autowired
    private GeneticAlgorithmService geneticAlgorithmService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private ErrorMatchBreakService errorMatchBreakService;
    @Autowired
    private ErrorMatchSameTimeService errorMatchSameTimeService;
    @Autowired
    private ErrorMatchSubjectService errorMatchSubjectService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private LivescoreService livescoreService;

    @Autowired
    private LivePenaltyService livePenaltyService;

    @Autowired
    private LiveCardService liveCardService;

    @Autowired
    private GroupStageInfoService groupStageInfoService;


    private List<SchoolYear> addSchoolYearToModel(){
        List<SchoolYear> schoolYears = schoolYearService.getAllSchoolYears();
        if(schoolYears.isEmpty()){
            return null;
        }
        return schoolYears;
    }

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

    private List<GroupStageMap> getGroupMap(RoundSchedule roundSchedule, Tournament tournament){
        List<GroupStageMap> totalGroup = new ArrayList<>();
        int numberGroup = 0;
        if(roundSchedule.getNumberMatch() == 48 || roundSchedule.getNumberMatch() == 24){
            numberGroup = 8;
        }else{
            numberGroup = 7;
        }
        for (int i = 0; i < numberGroup; i++) {
            char letter = (char) ('A' + i);
            List<GroupStageInfo> groupStageInfos =  groupStageInfoService.getRankingsByGroupTitle(tournament.getID(), String.valueOf(letter));
            GroupStageMap groupStageMap = new GroupStageMap();
            List<GroupStageInfoMap> groupStageInfoMaps = new ArrayList<>();
            for (GroupStageInfo groupStageInfo: groupStageInfos){
                GroupStageInfoMap teamsInRound = new GroupStageInfoMap();
                teamsInRound.setTeamID(groupStageInfo.getTeam().getID());
                String teamTitle = !groupStageInfo.getTeam().getClass2().isEmpty() ? groupStageInfo.getTeam().getClass1() +" - "+groupStageInfo.getTeam().getClass2() : groupStageInfo.getTeam().getClass1();
                teamsInRound.setTeamTitle(teamTitle);
                groupStageMap.setRoundTitle("group-stage");
                groupStageMap.setGroupTitle(groupStageInfo.getGroupTitle());
                teamsInRound.setTotalMatch(groupStageInfo.getTotalMatch());
                teamsInRound.setWin(groupStageInfo.getWin());
                teamsInRound.setLose(groupStageInfo.getLose());
                teamsInRound.setDraw(groupStageInfo.getDraw());
                teamsInRound.setGoalsScored(groupStageInfo.getGoalsScored());
                teamsInRound.setGoalsAgainst(groupStageInfo.getGoalsAgainst());
                teamsInRound.setGoalsDifference(groupStageInfo.getGoalsDifference());
                teamsInRound.setPoint(groupStageInfo.getPoint());
                teamsInRound.setRank(groupStageInfo.getRank());
                List<String> lastMatchs = new ArrayList<>();
                if(groupStageInfo.getLastMatch() != null){
                    for(String lastMatch : groupStageInfo.getLastMatch().split("--")){
                        if(lastMatch.contains("W")){
                            lastMatchs.add("Win");
                        }else if(lastMatch.contains("L")){
                            lastMatchs.add("Lose");
                        }else{
                            lastMatchs.add("Draw");
                        }
                    }
                }

                teamsInRound.setLastMatchs(lastMatchs);
                groupStageInfoMaps.add(teamsInRound);
            }
            List<Match_> matchListIngame = matchService.getMatchByType(tournament.getID(), "group-stage-"+String.valueOf(letter));
            groupStageMap.setGroupStageInfoMapList(groupStageInfoMaps);
            groupStageMap.setMatchsInGroup(matchListIngame);
            totalGroup.add(groupStageMap);
        }
        return totalGroup;
    }

    @GetMapping({"/", "/index","/home"})
    public String index(Model model){
        if(getTournamentCurrent() != null) {
            List<RoundSchedule> roundScheduleListInprogress = roundScheduleService.getRoundScheduleByStatus(getTournamentCurrent().getID(), "inprogress");
            if(!roundScheduleListInprogress.isEmpty()) {
                RoundSchedule roundScheduleInprogress = roundScheduleListInprogress.get(0);

                if (roundScheduleInprogress != null) {
                    model.addAttribute("roundScheduleInprogress", roundScheduleInprogress);
                    String roundTitle = "";
                    if (roundScheduleInprogress.getRoundTitle().contains("group-stage")) {
                        List<GroupStageMap> totalGroup = getGroupMap(roundScheduleInprogress, getTournamentCurrent());
                        model.addAttribute("totalGroup", totalGroup);
                    } else if (!roundScheduleInprogress.getRoundTitle().equals("final")) {
                        roundTitle = roundScheduleInprogress.getRoundTitle();
                        List<Match_> matchListIngame = matchService.getMatchByType(getTournamentCurrent().getID(), roundTitle);
                        model.addAttribute("matchListIngame", matchListIngame);
                    } else {
                        List<Match_> matchListIngame = matchService.getMatchByType(getTournamentCurrent().getID(), "third-place");
                        matchListIngame.addAll(matchService.getMatchByType(getTournamentCurrent().getID(), "final"));
                        List<String> matchType = new ArrayList<>();
                        matchType.add("Tranh hạng ba");
                        matchType.add("Chung kết");
                        model.addAttribute("matchType", matchType);
                        model.addAttribute("matchListIngame", matchListIngame);
                    }

                    List<RoundSchedule> roundSchedulesDone = roundScheduleService.getRoundScheduleByStatus(getTournamentCurrent().getID(), "done");

                    for (RoundSchedule roundSchedule : roundSchedulesDone) {
                        if (roundSchedule.getRoundTitle().equals("group-stage")) {
                            List<GroupStageMap> totalGroup = getGroupMap(roundSchedule, getTournamentCurrent());
                            model.addAttribute("group_stage_done", totalGroup);
                        } else if (!roundSchedule.getRoundTitle().equals("group-stage") && !roundSchedule.getRoundTitle().equals("final")) {
                            roundTitle = roundSchedule.getRoundTitle();
                            List<Match_> matchListIngame = matchService.getMatchByType(getTournamentCurrent().getID(), roundTitle);
                            model.addAttribute(roundTitle.replace("-", "_") + "_done", matchListIngame);
                        } else {
                            roundTitle = roundSchedule.getRoundTitle();
                            List<Match_> matchListIngame = matchService.getMatchByType(getTournamentCurrent().getID(), "third-place");
                            matchListIngame.addAll(matchService.getMatchByType(getTournamentCurrent().getID(), "final"));
                            model.addAttribute("final_done", matchListIngame);
                        }
                    }


                    /// thông số
                    List<Player> topScore = playerService.getPlayersByScoreDesc(getTournamentCurrent().getID());
                    List<Player> topAssist = playerService.getPlayersByAssistsDesc(getTournamentCurrent().getID());
                    List<Player> topRedCard = playerService.getPlayersByRedCardDesc(getTournamentCurrent().getID());
                    List<Player> topYellowCard = playerService.getPlayersByYellowCardDesc(getTournamentCurrent().getID());

                    List<PlayerStats> topScorePlayers = new ArrayList<>();
                    for (Player player : topScore) {
                        PlayerStats playerStats = new PlayerStats();
                        User user = userService.getUserById(player.getStudentID());
                        playerStats.setName(user.getName());
                        playerStats.setTeamID(player.getTeam().getID());
                        playerStats.setAvatar(user.getAvatar());
                        playerStats.setPlayerID(player.getID());
                        playerStats.setStudentID(player.getStudentID());
                        playerStats.setNumber(player.getGoal());
                        Team team = player.getTeam();
                        String teamTitle = !team.getClass2().isEmpty() ? team.getClass1() + " - " + team.getClass2() : team.getClass1();
                        playerStats.setTeamTitle(teamTitle);
                        topScorePlayers.add(playerStats);
                    }

                    List<PlayerStats> topAssistPlayers = new ArrayList<>();
                    for (Player player : topAssist) {
                        PlayerStats playerStats = new PlayerStats();
                        User user = userService.getUserById(player.getStudentID());
                        playerStats.setName(user.getName());
                        playerStats.setTeamID(player.getTeam().getID());
                        playerStats.setAvatar(user.getAvatar());
                        playerStats.setPlayerID(player.getID());
                        playerStats.setStudentID(player.getStudentID());
                        playerStats.setNumber(player.getAssists());
                        Team team = player.getTeam();
                        String teamTitle = !team.getClass2().isEmpty() ? team.getClass1() + " - " + team.getClass2() : team.getClass1();
                        playerStats.setTeamTitle(teamTitle);
                        topAssistPlayers.add(playerStats);
                    }

                    List<PlayerStats> topRedCardPlayers = new ArrayList<>();
                    for (Player player : topRedCard) {
                        PlayerStats playerStats = new PlayerStats();
                        User user = userService.getUserById(player.getStudentID());
                        playerStats.setName(user.getName());
                        playerStats.setAvatar(user.getAvatar());
                        playerStats.setTeamID(player.getTeam().getID());
                        playerStats.setPlayerID(player.getID());
                        playerStats.setStudentID(player.getStudentID());
                        playerStats.setNumber(player.getRedCard());
                        Team team = player.getTeam();
                        String teamTitle = !team.getClass2().isEmpty() ? team.getClass1() + " - " + team.getClass2() : team.getClass1();
                        playerStats.setTeamTitle(teamTitle);
                        topRedCardPlayers.add(playerStats);
                    }

                    List<PlayerStats> topYellowCardPlayers = new ArrayList<>();
                    for (Player player : topYellowCard) {
                        PlayerStats playerStats = new PlayerStats();
                        User user = userService.getUserById(player.getStudentID());
                        playerStats.setName(user.getName());
                        playerStats.setTeamID(player.getTeam().getID());
                        playerStats.setAvatar(user.getAvatar());
                        playerStats.setPlayerID(player.getID());
                        playerStats.setStudentID(player.getStudentID());
                        playerStats.setNumber(player.getYellowCard());
                        Team team = player.getTeam();
                        String teamTitle = !team.getClass2().isEmpty() ? team.getClass1() + " - " + team.getClass2() : team.getClass1();
                        playerStats.setTeamTitle(teamTitle);
                        topYellowCardPlayers.add(playerStats);
                    }
                    model.addAttribute("topScore", topScorePlayers);
                    model.addAttribute("topAssist", topAssistPlayers);
                    model.addAttribute("topRedCard", topRedCardPlayers);
                    model.addAttribute("topYellowCard", topYellowCardPlayers);
                }
            }
        }

        model.addAttribute("title","Tournament IT TDTU");
        return "index";
    }

    public SchoolYear getSchoolYearRequired(String year){
        System.out.println("year: "+year);
        if(year == null){
            return null;
        }

        String regex = "^(\\d{4})-(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        if(pattern.matcher(year).matches()) {
            Integer year1 = Integer.parseInt(year.split("-")[0]);
            Integer year2 = Integer.parseInt(year.split("-")[1]);
            SchoolYear schoolYear = schoolYearService.getSchoolYearByYear1AndYear2(year1, year2);

            if (schoolYear != null) {
                return schoolYear;
            }
        }
        return null;
    }

    public Tournament getTournamentRequired(SchoolYear schoolYear){
        Tournament tournament;
        if (schoolYear != null){
            tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());
            if(tournament != null){
                return tournament;
            }
        }
        return null;
    }

    @GetMapping("/login")
    public String login(Model model,Authentication authentication){
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/";
        }

        model.addAttribute("title","Đăng nhập");
        return "login";
    }

    @GetMapping("/create_tournament")
    public String createTournament(Model model){

        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
        if(schoolYear != null){
            return "redirect:/";
        }
        List<TakeIn> takeInList = takeInService.getAllTakeIn();
        model.addAttribute("takeIns",takeInList);
        Tournament tournament = new Tournament();
        model.addAttribute("tournament",tournament);
        model.addAttribute("title","Tạo giải đấu");
        return "createTour";
    }

    @PostMapping("/create_tournament")
    public String processTournament(
            @Valid @ModelAttribute("tournament") Tournament tournament,
            BindingResult result,
            @RequestParam(value = "tournamentTakeIns",required = false) List<String> takeInIds,
            Model model) {

        if (result.hasErrors()) {

            if (takeInIds == null) {
                model.addAttribute("takeInsError", "Chưa chọn niên khóa tham gia");
            }
            else {
                model.addAttribute("takeInIds", takeInIds);
            }

            if(tournament.getSchoolYear().getYear1() != null && tournament.getSchoolYear().getYear2() != null ){
                if(tournament.getSchoolYear().getYear1() >= tournament.getSchoolYear().getYear2()){
                    model.addAttribute("schoolYearError", "Nhập năm học kỳ sai");
                }
                else{
                    model.addAttribute("schoolYear", tournament.getSchoolYear());
                }
            }


            if (tournament.getMinPlayerPerTeam() != null && tournament.getMaxPlayerPerTeam() != null ){
                if(tournament.getMinPlayerPerTeam() > tournament.getMaxPlayerPerTeam()){
                    model.addAttribute("MinMaxError", "Số thành viên tối đa không được nhỏ hơn số thành viên tối thiểu");
                }
                if(tournament.getMinPlayerPerTeam() == tournament.getMaxPlayerPerTeam()){
                    model.addAttribute("MinMaxError", "Số thành viên tối đa không được bằng số thành viên tối thiểu");
                }
            }

            List<TakeIn> takeInList = takeInService.getAllTakeIn();
            model.addAttribute("takeIns", takeInList);

            model.addAttribute("tournament", tournament);

            model.addAttribute("title", "Tạo giải đấu");
            return "createTour";
        }

        SchoolYear schoolYear_ = schoolYearService.getSchoolYearByYear1AndYear2(tournament.getSchoolYear().getYear1(),tournament.getSchoolYear().getYear2());
        if(schoolYear_ != null){
            List<TakeIn> takeInList = takeInService.getAllTakeIn();
            model.addAttribute("takeIns", takeInList);
            model.addAttribute("takeInIds", takeInIds);
            model.addAttribute("schoolYearError", "Năm học đã tổ chức giải đấu");
            return "createTour";
        }

        if(tournament.getSchoolYear().getYear2() != tournament.getSchoolYear().getYear1() + 1){
            List<TakeIn> takeInList = takeInService.getAllTakeIn();
            model.addAttribute("takeIns", takeInList);
            model.addAttribute("takeInIds", takeInIds);
            model.addAttribute("schoolYearError", "Nhập năm học kỳ sai");
            return "createTour";
        }


        if(tournament.getID() == null){

            SchoolYear schoolYear = new SchoolYear(tournament.getSchoolYear().getYear1(),tournament.getSchoolYear().getYear2(),"1");

            schoolYearService.createSchoolYear(schoolYear);

            schoolYear = schoolYearService.getSchoolYearByCurrent("1");

            tournament.setSchoolYear(schoolYear);
            tournament.setStatus("open");

            tournamentService.createTournament(tournament);

            tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());

            Set<TournamentTakeIn> tournamentTakeIns = new HashSet<>();

            for(String takeInId : takeInIds){
                TournamentTakeIn tournamentTakeIn = new TournamentTakeIn();
                TakeIn takeIn = takeInService.getTakeInById(takeInId);
                tournamentTakeIn.setTournament(tournament);
                tournamentTakeIn.setTakeIn(takeIn);
                tournamentTakeIn.setID(takeIn.getID()+"_"+schoolYear.getID());
                tournamentTakeIns.add(tournamentTakeIn);
            }

            tournament.setTournamentTakeIns(tournamentTakeIns);
            tournamentService.createTournament(tournament);
        }else {

            SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
            schoolYear.setYear1(tournament.getSchoolYear().getYear1());
            schoolYear.setYear2(tournament.getSchoolYear().getYear2());
            tournament.setSchoolYear(schoolYear);
            tournament.setStatus("open");

            tournamentTakeInService.deleteTournamentTakeInsByTournament(tournament.getID());

            Set<TournamentTakeIn> tournamentTakeIns = new HashSet<>();
            for(String takeInId : takeInIds){
                TakeIn takeIn = takeInService.getTakeInById(takeInId);
                TournamentTakeIn tournamentTakeIn = new TournamentTakeIn(takeIn.getID()+"_"+schoolYear.getID(),tournament,takeIn);
                tournamentTakeIns.add(tournamentTakeIn);
            }
            tournament.setTournamentTakeIns(tournamentTakeIns);
            tournamentService.createTournament(tournament);
        }

        return "redirect:/";
    }

    @GetMapping("/update_tournament")
    public String updateTournament(Model model){

        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");

        List<TakeIn> takeInList = takeInService.getAllTakeIn();
        model.addAttribute("takeIns",takeInList);

        Tournament tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());
        model.addAttribute("tournament",tournament);

        List<TournamentTakeIn> tournamentTakeIns = tournamentTakeInService.getTournamentTakeInByTournament(tournament.getID());
        List<String> takeInIds = new ArrayList<>();

        for (TournamentTakeIn tournamentTakeIn: tournamentTakeIns){
            takeInIds.add(tournamentTakeIn.getTakeIn().getID());
        }
        model.addAttribute("takeInIds",takeInIds);

        model.addAttribute("title","Thay đổi thông tin giải đấu");
        return "updateTour";
    }

    @PostMapping("/update_tournament")
    public String processUpdateTournament(
            @Valid @ModelAttribute("tournament") Tournament tournament,
            BindingResult result,
            @RequestParam(value = "tournamentTakeIns",required = false) List<String> takeInIds,
            Model model) {

        System.out.println("update tour");

        if (result.hasErrors()) {

            if (takeInIds == null) {
                model.addAttribute("takeInsError", "Chưa chọn niên khóa tham gia");
            }
            else {
                model.addAttribute("takeInIds", takeInIds);
            }

            if(tournament.getSchoolYear().getYear1() != null && tournament.getSchoolYear().getYear2() != null ){
                if(tournament.getSchoolYear().getYear1() >= tournament.getSchoolYear().getYear2()){
                    model.addAttribute("schoolYearError", "Nhập năm học kỳ sai");
                }
                else{
                    model.addAttribute("schoolYear", tournament.getSchoolYear());
                }
            }

            if (tournament.getMinPlayerPerTeam() != null && tournament.getMaxPlayerPerTeam() != null ){
                if(tournament.getMinPlayerPerTeam() > tournament.getMaxPlayerPerTeam()){
                    model.addAttribute("MinMaxError", "Số thành viên tối đa không được nhỏ hơn số thành viên tối thiểu");
                }
                if(tournament.getMinPlayerPerTeam() == tournament.getMaxPlayerPerTeam()){
                    model.addAttribute("MinMaxError", "Số thành viên tối đa không được bằng số thành viên tối thiểu");
                }
            }

            List<TakeIn> takeInList = takeInService.getAllTakeIn();
            model.addAttribute("takeIns", takeInList);

            model.addAttribute("tournament", tournament);

            model.addAttribute("title", "Tạo giải đấu");
            return "updateTour";
        }

        if(tournament.getSchoolYear().getYear2() != tournament.getSchoolYear().getYear1() + 1){
            List<TakeIn> takeInList = takeInService.getAllTakeIn();
            model.addAttribute("takeIns", takeInList);
            model.addAttribute("takeInIds", takeInIds);
            model.addAttribute("schoolYearError", "Nhập năm học kỳ sai");
            return "updateTour";
        }

        SchoolYear schoolYear = tournament.getSchoolYear();
        schoolYearService.saveSchoolYear(schoolYear);

        tournament.setStatus("open");

        tournamentTakeInService.deleteTournamentTakeInsByTournament(tournament.getID());
        Set<TournamentTakeIn> tournamentTakeIns = new HashSet<>();

        for(String takeInId : takeInIds){
            TournamentTakeIn tournamentTakeIn = new TournamentTakeIn();
            TakeIn takeIn = takeInService.getTakeInById(takeInId);
            tournamentTakeIn.setTournament(tournament);
            tournamentTakeIn.setTakeIn(takeIn);
            tournamentTakeIn.setID(takeIn.getID()+"_"+schoolYear.getID());
            tournamentTakeIns.add(tournamentTakeIn);
        }

        tournament.setTournamentTakeIns(tournamentTakeIns);
        tournamentService.createTournament(tournament);

        return "redirect:/";
    }

    @GetMapping("/data")
    public String addData(Model model){

        String nameProcess1 = "addStudent";
        if(!uploadProcessService.canThread(nameProcess1)){
            model.addAttribute("studentError","Đang thêm sinh viên");
        }

        String nameProcess2 = "addPlanYear";
        if(!uploadProcessService.canThread(nameProcess2)){
            model.addAttribute("planYearError","Đang thêm lịch nghỉ");
        }

        String nameProcess3 = "addWeek";
        if(!uploadProcessService.canThread(nameProcess3)){
            model.addAttribute("weekError","Đang thêm danh sách tuần");
        }

        String nameProcess4 = "addScheduleSubject";
        if(!uploadProcessService.canThread(nameProcess4)){
            model.addAttribute("scheduleSubjectError","Đang thêm môn học");
        }

        SchoolYear schoolYears = schoolYearService.getSchoolYearByCurrent("1");
        if(schoolYears != null){
            model.addAttribute("student",true);
        }else{
            model.addAttribute("student",false);
        }

        model.addAttribute("title","Thêm dữ liệu");
        return "addData";
    }

    @PostMapping("/dataUpload")
    public String saveData(
            @RequestParam(value = "student",required = false) MultipartFile student,
            @RequestParam(value = "planYear",required = false) MultipartFile planYear,
            @RequestParam(value = "week",required = false) MultipartFile week,
            @RequestParam(value = "scheduleSubject",required = false) MultipartFile scheduleSubject,
            RedirectAttributes redirectAttributes,
            Principal principal
    ){

        int studentError = 0;
        int planYearError = 0;
        int weekError = 0;
        int scheduleSubjectError = 0;

        if( student.isEmpty() &&
            planYear.isEmpty() && week.isEmpty() &&
            scheduleSubject.isEmpty())
        {
            return "redirect:/student";
        }



        if(!student.isEmpty()){
            if (student.getSize() > 10485760) {
                studentError = 1;
                redirectAttributes.addFlashAttribute("studentError", "File có kích thước lớn hơn 10M");
            }

            String contentType = student.getContentType();
            if (!"text/csv".equals(contentType) && !"application/vnd.ms-excel".equals(contentType) && studentError != 1) {
                studentError = 1;
                redirectAttributes.addFlashAttribute("studentError", "File không phải csv");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(student.getInputStream()))) {
                String firstLine = reader.readLine();
                String[] header = firstLine.split(",");

                if(header.length != 6 && studentError != 1){
                    studentError = 1;
                    redirectAttributes.addFlashAttribute("studentError", "Không phải file danh sách sinh viên");
                }

                if(!header[0].equals("ID") && !header[1].equals("name") && !header[2].equals("gender") && !header[3].equals("inTake") && !header[4].equals("classID") && !header[5].equals("mode") && studentError != 1)
                {
                    studentError = 1;
                    redirectAttributes.addFlashAttribute("studentError", "Không phải file danh sách sinh viên");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(!planYear.isEmpty()) {
            if (planYear.getSize() > 10485760) {
                planYearError = 1;
                redirectAttributes.addFlashAttribute("planYearError", "File có kích thước lớn hơn 10M");
            }
            String contentType = planYear.getContentType();
            if (!"text/csv".equals(contentType) && !"application/vnd.ms-excel".equals(contentType) && planYearError != 1) {
                planYearError = 1;
                redirectAttributes.addFlashAttribute("planYearError", "File không phải csv");
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(planYear.getInputStream()))) {
                String firstLine = reader.readLine();
                String[] header = firstLine.split(",");

                if(header.length != 4  && planYearError != 1){
                    planYearError = 1;
                    redirectAttributes.addFlashAttribute("planYearError", "Không phải file ngày nghỉ");
                }
                if(!header[0].equals("name") && !header[1].equals("week") && !header[2].equals("date") && !header[3].equals("num_date")  && planYearError != 1)
                {
                    planYearError = 1;
                    redirectAttributes.addFlashAttribute("planYearError", "Không phải file ngày nghỉ");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(!week.isEmpty()) {
            if (week.getSize() > 10485760) {
                weekError = 1;
                redirectAttributes.addFlashAttribute("weekError", "File có kích thước lớn hơn 10M");
            }
            String contentType = week.getContentType();
            if (!"text/csv".equals(contentType) && !"application/vnd.ms-excel".equals(contentType) && weekError != 1) {
                redirectAttributes.addFlashAttribute("weekError", "File không phải csv");
                weekError = 1;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(week.getInputStream()))) {
                String firstLine = reader.readLine();
                String[] header = firstLine.split(",");

                if(header.length != 4 && weekError != 1){
                    redirectAttributes.addFlashAttribute("weekError", "Không phải file danh sách tuần");
                    weekError = 1;
                }
                if(!header[0].equals("semester") && !header[1].equals("num_week") && !header[2].equals("start_date") && !header[3].equals("end_date") && weekError != 1)
                {
                    redirectAttributes.addFlashAttribute("weekError", "Không phải file danh sách tuần");
                    weekError = 1;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        if(!scheduleSubject.isEmpty()) {
            if (scheduleSubject.getSize() > 10485760) {
                redirectAttributes.addFlashAttribute("scheduleSubjectError", "File có kích thước lớn hơn 10M");
                scheduleSubjectError = 1;
            }
            String contentType = scheduleSubject.getContentType();
            if (!"text/csv".equals(contentType) && !"application/vnd.ms-excel".equals(contentType) && scheduleSubjectError != 1) {
                redirectAttributes.addFlashAttribute("scheduleSubjectError", "File không phải csv");
                scheduleSubjectError = 1;
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(scheduleSubject.getInputStream()))) {
                String firstLine = reader.readLine();
                String[] header = firstLine.split(",");

                if(header.length != 8 && scheduleSubjectError != 1){
                    redirectAttributes.addFlashAttribute("scheduleSubjectError", "Không phải file lịch học");
                    scheduleSubjectError = 1;
                }
                if(!header[0].equals("ID") && !header[1].equals("subjectID") && !header[2].equals("name") && !header[3].equals("shift") && !header[4].equals("weekday") && !header[5].equals("week") && !header[6].equals("semester")&& !header[7].equals("students") && scheduleSubjectError != 1)
                {
                    redirectAttributes.addFlashAttribute("scheduleSubjectError", "Không phải file lịch học");
                    scheduleSubjectError = 1;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }


        if(studentError == 0 && !student.isEmpty()){
            fileService.save(student,"student");
            String nameProcess = "addStudent";
            if(uploadProcessService.canThread(nameProcess)) {
                uploadProcessService.setThreadStart(nameProcess);
                try{
                    processFileService.processFileStudent().thenRun(() ->
                            uploadProcessService.setThreadFinish(nameProcess)
                    );
                }catch (Exception e) {
                    uploadProcessService.setThreadFinish(nameProcess);
                    redirectAttributes.addFlashAttribute("studentError", "File đang chạy");
                }
            } else {
                redirectAttributes.addFlashAttribute("studentError", "File đang chạy");
                return "redirect:/data";
            }
        }

        if(planYearError == 0 && !planYear.isEmpty()){
            fileService.save(planYear,"planYear");
            String nameProcess = "addPlanYear";
            if(uploadProcessService.canThread(nameProcess)) {
                uploadProcessService.setThreadStart(nameProcess);
                try{
                    processFileService.processFilePlanYear().thenRun(() ->
                            uploadProcessService.setThreadFinish(nameProcess)
                    );
                }catch (Exception e) {
                    uploadProcessService.setThreadFinish(nameProcess);
                    redirectAttributes.addFlashAttribute("planYearError", "File đang chạy");
                }
            } else {
                redirectAttributes.addFlashAttribute("planYearError", "File đang chạy");
                return "redirect:/data";
            }
        }

        if(weekError == 0 && !week.isEmpty()){
            fileService.save(week,"week");
            String nameProcess = "addWeek";
            if(uploadProcessService.canThread(nameProcess)) {
                uploadProcessService.setThreadStart(nameProcess);
                try{
                    processFileService.processFileWeek().thenRun(() ->
                            uploadProcessService.setThreadFinish(nameProcess)
                    );
                }catch (Exception e) {
                    uploadProcessService.setThreadFinish(nameProcess);
                    redirectAttributes.addFlashAttribute("weekError", "File đang chạy");
                }
            } else {
                redirectAttributes.addFlashAttribute("weekError", "File đang chạy");
                return "redirect:/data";
            }
        }

        if(scheduleSubjectError == 0 && !scheduleSubject.isEmpty()){
            fileService.save(scheduleSubject,"scheduleSubject");
            String nameProcess = "addScheduleSubject";
            if(uploadProcessService.canThread(nameProcess)) {
                uploadProcessService.setThreadStart(nameProcess);
                try{
                    processFileService.processFileScheduleSubject().thenRun(() ->
                            uploadProcessService.setThreadFinish(nameProcess)
                    );
                }catch (Exception e) {
                    uploadProcessService.setThreadFinish(nameProcess);
                    redirectAttributes.addFlashAttribute("scheduleSubjectError", "File đang chạy");
                }
            } else {
                redirectAttributes.addFlashAttribute("scheduleSubjectError", "File đang chạy");
                return "redirect:/data";
            }
        }

        if(studentError == 1 || planYearError == 1 ||
                weekError == 1 || scheduleSubjectError == 1
        ){
            return "redirect:/data";
        }

        return "redirect:/student";
    }

    @GetMapping("/student")
    public String viewDataStudentSearch(Model model,
            @RequestParam(name = "page",required = false) String pageParam,
            @RequestParam(name = "studentID",required = false) String studentID,
            @RequestParam(name = "takeIn",required = false) String takeInID,
            @RequestParam(name = "class",required = false) String class_){

        Integer page;

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        if(pageParam != null ){
            if(pageParam.isEmpty() || !pattern.matcher(pageParam).matches()){
                page = 0;
            }
            else {
                page = Integer.parseInt(pageParam) - 1;
            }
        }else {
            page = 0;
        }

        if(studentID != null){
            if(studentID.isEmpty()){
                studentID = null;
            }
        }

        if(takeInID != null){
            if(takeInID.isEmpty()){
                takeInID = null;
            }
        }

        if(class_ != null){
            if(class_.isEmpty()){
                class_ = null;
            }
        }

        List<Student> students;
        if(page < 0){
            students = studentService.getStudentsByIDAndTakeInIDAndClass(0,100,studentID,takeInID,class_);
        }else {
            students = studentService.getStudentsByIDAndTakeInIDAndClass(page,100,studentID,takeInID,class_);
        }

        List<StudentInfo> studentInfoList = new ArrayList<>();
        for (Student student: students){
            User user = userService.getUserById(student.getID());
            studentInfoList.add(new StudentInfo(student.getID(),user.getName(),student.getTakeIn(),student.getClassID(),student.getMode()));
        }

        students = null;

        model.addAttribute("students",studentInfoList);

        model.addAttribute("title","Danh sách sinh viên");
        return "dataStudent";
    }

    @GetMapping("/schedule")
    public String viewDataSchedule(Model model,
           @RequestParam(name = "page",required = false) String pageParam,
           @RequestParam(name = "subject",required = false) String subjectName,
           @RequestParam(name = "shift",required = false) String shift,
           @RequestParam(name = "semester",required = false) String semester,
           @RequestParam(name = "weekday",required = false) String weekday){

        Integer page;

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        if(pageParam != null ){
            if(pageParam.isEmpty() || !pattern.matcher(pageParam).matches()){
                page = 0;
            }
            else {
                page = Integer.parseInt(pageParam) - 1;
            }
        }else {
            page = 0;
        }

        if(subjectName != null){
            if(subjectName.isEmpty()){
                subjectName = null;
            }
        }

        if(shift != null){
            if(shift.isEmpty()){
                shift = null;
            }
        }

        if(weekday != null){
            if(weekday.isEmpty()){
                weekday = null;
            }
        }

        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");

        List<String> semesterSearch;

        if(semester != null){
            if(semester.isEmpty()){
                semesterSearch = null;
            }else{
                if(pattern.matcher(pageParam).matches()){

                    List<String> semesterEven = new ArrayList<>();
                    semesterEven.add("hk2");
                    semesterEven.add("hk4");
                    semesterEven.add("hk6");
                    semesterEven.add("hk8");

                    List<String> semesterOdd = new ArrayList<>();
                    semesterOdd.add("hk1");
                    semesterOdd.add("hk3");
                    semesterOdd.add("hk5");
                    semesterOdd.add("hk7");

                    Map<String,List<String>> semesterDictionary = new HashMap<>();
                    semesterDictionary.put("1",semesterOdd);
                    semesterDictionary.put("2",semesterEven);
                    if(Integer.parseInt(semester) < 0 ){
                        semesterSearch = null;
                    }else{
                        if(Integer.parseInt(semester) % 2 == 0){
                            semesterSearch = semesterDictionary.get("2");
                        }else{
                            semesterSearch = semesterDictionary.get("1");
                        }
                    }
                }else{
                    semesterSearch = null;
                }
            }
        }else {
            semesterSearch = null;
        }

        Integer schoolYearID;

        if(schoolYear != null){
            schoolYearID = schoolYear.getID();
            model.addAttribute("schoolYear",schoolYear);
        }else {
            schoolYearID = null;
        }

        List<ScheduleSubject> scheduleSubjects;

        if(page < 0){
            scheduleSubjects = scheduleSubjectService.getScheduleSubjectsByNameAndShiftAndWeekInSearch(0,100,subjectName,shift,weekday,semesterSearch,schoolYearID);
        }else {
            scheduleSubjects = scheduleSubjectService.getScheduleSubjectsByNameAndShiftAndWeekInSearch(page,100,subjectName,shift,weekday,semesterSearch,schoolYearID);
        }

//        List<ScheduleSubject> scheduleSubjects = scheduleSubjectService.getAllScheduleSubjects();

        model.addAttribute("schedules",scheduleSubjects);

        model.addAttribute("title","Lịch học");
        return "dataSchedule";
    }

    @GetMapping("/schedule/{scheduleSubjectID}")
    public String viewDataScheduleInfo(Model model,
        @PathVariable(name = "scheduleSubjectID") String scheduleSubjectID){

        ScheduleSubject scheduleSubject = scheduleSubjectService.getScheduleSubjectById(scheduleSubjectID);
        model.addAttribute("scheduleSubject",scheduleSubject);

        List<ScheduleStudent> scheduleStudents = scheduleStudentService.getScheduleStudentBySubject(scheduleSubject.getID());
        List<StudentInfo> studentInfos = new ArrayList<>();
        for(ScheduleStudent scheduleStudent: scheduleStudents){
            Student student = scheduleStudent.getStudent();
            User user = userService.getUserById(student.getID());
            studentInfos.add(new StudentInfo(student.getID(),user.getName(),student.getTakeIn(),student.getClassID(),student.getMode()));
        }
        model.addAttribute("students",studentInfos);
        model.addAttribute("title","Thông tin môn học");
        return "dataScheduleInfo";
    }

    @GetMapping("/week")
    public String viewDataWeek(Model model){

        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");

        if(schoolYear != null) {
            List<SemesterWeek> semesterWeeks = semesterWeekService.getSemesterWeekBySchoolYear(schoolYear.getID());

            List<PlanYear> planYears = planYearService.getPlanYearBySchoolYear(schoolYear.getID());

            model.addAttribute("semesterWeeks", semesterWeeks);

            model.addAttribute("planYears", planYears);
        }
        model.addAttribute("title","Thông tin tuần học");
        return "dataWeek";
    }

    @GetMapping("/personal_information")
    public String personalInfo(Model model,Principal principal
    ){
        model.addAttribute("title","Thông tin cá nhân");
        Account account = accountService.getAccountById(principal.getName());
        String role = account.getRole();
        model.addAttribute("role",role);
        if(role.equals("ROLE_STUDENT")){
            Student student = studentService.getStudentById(principal.getName());
            model.addAttribute("student",student);
        }
        if(role.equals("ROLE_PLAYER")){
            Player player = playerService.getPlayerByStudentIDAndTournamentID(principal.getName(),getTournamentCurrent().getID());
            model.addAttribute("player",player);
            Student student = studentService.getStudentById(principal.getName());
            model.addAttribute("student",student);
            Team team = player.getTeam();
            model.addAttribute("team",team);
            SchoolYear schoolYear = player.getTournament().getSchoolYear();
            model.addAttribute("season",schoolYear);
        }
        return "personalinfo";
    }

    @GetMapping({"/user/{studentID}"})
    public String userInfo(Model model,
       @PathVariable(name = "studentID") String studentID,
       @ModelAttribute("user_current") User userCurrent,
       Authentication authentication
    ){
        if(userCurrent != null){
            if(userCurrent.getID().equals(studentID)){
                return "redirect:/personal_information";
            }
        }

        Account account = accountService.getAccountById(studentID);
        if(account != null){
            String role = account.getRole();
            if(!role.equals("ROLE_MANAGER")){
                User user = userService.getUserById(studentID);
                model.addAttribute("userInformation",user);
                Student student = studentService.getStudentById(studentID);
                model.addAttribute("student",student);
                Player player = playerService.getPlayerByStudentIDAndTournamentID(studentID,getTournamentCurrent().getID());
                if (player != null){
                    Team team = player.getTeam();
                    model.addAttribute("team",team);
                    model.addAttribute("player",player);
                    SchoolYear schoolYear = player.getTournament().getSchoolYear();
                    model.addAttribute("season",schoolYear);
                }
                model.addAttribute("title","Thông tin cá nhân");
                return "userInfo";
            }
        }
        return "redirect:/";
    }


    @PostMapping("/update_personal_information")
    public String updatePersonalInfo(
            @RequestParam(value = "changeAvatar",required = false) MultipartFile changeAvatar,
            @RequestParam(value = "height",required = false) String height,
            @RequestParam(value = "weight",required = false) String weight,
            Principal principal
    ){

        if(changeAvatar != null){
            if(!changeAvatar.isEmpty()){
                String contentType = changeAvatar.getContentType();
                if(contentType != null && contentType.startsWith("image/")){
                    avatarUpload.save(changeAvatar);
                    User user = userService.getUserById(principal.getName());
                    user.setAvatar(avatarUpload.changeAvatarFileName(changeAvatar));
                    userService.createUser(user);
                }
            }
        }

        Player player = playerService.getPlayerByStudentIDAndTournamentID(principal.getName(), getTournamentCurrent().getID());
        if(player != null){
            if(height != null){
                if(!height.isEmpty()){
                    player.setHeight(Integer.parseInt(height));
                }
            }
            if(weight != null){
                if(!weight.isEmpty()){
                    player.setWeight(Integer.parseInt(weight));
                }
            }

            playerService.createPlayer(player);
        }

        return "redirect:/personal_information";
    }

    @GetMapping("/sign_up_team")
    @PreAuthorize("hasAnyRole('STUDENT','PLAYER')")
    public String signUpTeamGet(Model model,
        @ModelAttribute("studentCurrent") Student student,
        @ModelAttribute("tournament_current") Tournament tournament,
        @RequestParam(name = "error", required = false) String error_
    ){

        if(playerService.getPlayerByStudentIDAndTournamentID(student.getID(), getTournamentCurrent().getID()) != null){
            return "redirect:/manager_team";
        }

        model.addAttribute("classID_", student.getClassID());

        Set<TournamentTakeIn> tournamentTakeIns = tournament.getTournamentTakeIns();
        Set<String> checkTakeIn = tournamentTakeIns.stream()
                .map(tournamentTakeIn -> tournamentTakeIn.getTakeIn().getID())
                .collect(Collectors.toSet());

        if(!checkTakeIn.contains(student.getTakeIn().getID())){

            model.addAttribute("allow","false");
        }else{

            model.addAttribute("allow","true");

            if(userService.getUserById(student.getID()).getGender().equals("male")){
                model.addAttribute("gender","male");
            }else{
                model.addAttribute("gender","female");
            }
        }

        if (!model.containsAttribute("signUpTeamInfo")) {
            model.addAttribute("signUpTeamInfo", new SignUpTeam());
        }

        model.addAttribute("title","Đăng ký đội");
        return "signupTeam";
    }

    @PostMapping("/sign_up_team")
    @PreAuthorize("hasAnyRole('STUDENT','PLAYER')")
    public String signUpTeamPost(Model model,
         RedirectAttributes redirectAttributes,
         @RequestParam(value = "class1",required = false) String class1,
         @RequestParam(value = "class2",required = false) String class2,
         @RequestParam(value = "captain",required = false) String captainTeam,
         @RequestParam(value = "players",required = false) List<String> playerIDs,
         @RequestParam(value = "playerNumbers",required = false) List<Integer> playerNumbers,
         @ModelAttribute("tournament_current") Tournament tournament_current,
         @ModelAttribute("user_current") User userCurrent){

        Integer error = 0;

        if(class1 == null || captainTeam == null || playerIDs == null || playerNumbers == null){
            error = 1;
            redirectAttributes.addFlashAttribute("error", "Chưa đăng ký đội");

        }else {
            if(class1.isEmpty()){
                error = 1;
                redirectAttributes.addFlashAttribute("error", "Chưa có lớp");
            }

            if(playerIDs.isEmpty() && error == 0){
                error = 1;
                redirectAttributes.addFlashAttribute("error", "Chưa chọn cầu thủ");
            }
        }

        Integer maxPlayerInTeam = tournament_current.getMaxPlayerPerTeam();
        Integer minPlayerInTeam = tournament_current.getMinPlayerPerTeam();

        if((playerIDs.size() < minPlayerInTeam || playerIDs.size() > maxPlayerInTeam) && error == 0){
            String msg = "Thành viên phải nhiều hơn "+minPlayerInTeam.toString()+" và ít hơn "+maxPlayerInTeam.toString();
            redirectAttributes.addFlashAttribute("error", msg);
            error = 1;
        }

        if((playerNumbers.size() != playerIDs.size()) && error == 0){
            String msg = "Chọn số áo đầy đủ";
            redirectAttributes.addFlashAttribute("error", msg);
            error = 1;
        }

        if(captainTeam.isEmpty() && error == 0){
            redirectAttributes.addFlashAttribute("error", "Chưa chọn đội trưởng");
            error = 1;
        }

        if(class2 != null){
            if (!class2.isEmpty()){
                Team TeamOfClass2 = teamService.getTeamByClass1OrClass2(getTournamentCurrent().getID(),class2,class2);
                if(TeamOfClass2 != null){
                    if(!TeamOfClass2.getClass1().equals(class1)){
                        redirectAttributes.addFlashAttribute("error", "Không thể thêm thành viên của đội đã đăng ký");
                        error = 1;
                    }
                }
            }
        }

        if(error == 1){
            SignUpTeam signUpTeam = new SignUpTeam();
            signUpTeam.setClass1(class1);
            if(class2 != null){
                signUpTeam.setClass2(class2);
            }
            signUpTeam.setCaptain(captainTeam);

            List<Player> players = new ArrayList<>();

            for(int i = 0; i < playerIDs.size();i++){
                Player player = new Player();
                String Id = playerIDs.get(i);
                Student student = studentService.getStudentById(Id);
                User user = userService.getUserById(Id);
                player.setStudentID(student.getID());
                player.setTournament(getTournamentCurrent());
                player.setName(user.getName());
                player.setClassID(student.getClassID());
                player.setPlayerNumber(playerNumbers.get(i));
                players.add(player);
            }

            signUpTeam.setPlayers(players);
            redirectAttributes.addFlashAttribute("signUpTeamInfo", signUpTeam);
            return "redirect:/sign_up_team";
        }

        Student studentCaptain = studentService.getStudentById(captainTeam);

        if(studentCaptain.getClassID().equals(class2)){
            String team1Tam = class1;
            class1 = class2;
            class2 = team1Tam;
        }

        Team team = new Team();
        team.setTournament(tournament_current);
        team.setClass1(class1);

        if(class2 != null){
            team.setClass2(class2);
        }

        team.setStatus("signup");
        team.setStop(0);
        team.setTotalPlayer(playerIDs.size());
        team = teamService.createTeam(team);

        Integer checkStudentClass1 = 0, checkStudentClass2 = 0;

        for(int i = 0; i < playerIDs.size(); i++){
            Student student = studentService.getStudentById(playerIDs.get(i));
            User user = userService.getUserById(playerIDs.get(i));
            Account account = accountService.getAccountById(playerIDs.get(i));
            account.setRole("ROLE_PLAYER");
            accountService.save(account);
            Player player = new Player();
            player.setStudentID(student.getID());
            player.setTournament(getTournamentCurrent());
            player.setName(user.getName());
            player.setClassID(student.getClassID());
            if(class1.equals(student.getClassID())){
                checkStudentClass1 = 1;
            }
            if(class2 != null){
                if(class2.equals(student.getClassID())){
                    checkStudentClass2 = 1;
                }
            }
            player.setPlayerNumber(playerNumbers.get(i));
            player.setTeam(team);
            player.setStatus("signup");
            player.setGoal(0);
            player.setAssists(0);
            player.setYellowCard(0);
            player.setRedCard(0);
            player.setHeight(0);
            player.setWeight(0);
            if(playerIDs.get(i).equals(captainTeam)){
                player.setCaptain(1);
            }
            else {
                player.setCaptain(0);
            }
            playerService.createPlayer(player);
        }

        if(checkStudentClass1 == 0){
            String class2Tam = team.getClass2();
            team.setClass1(class2Tam);
            team.setClass2("");
        }
        if(checkStudentClass2 == 0){
            team.setClass2("");
        }
        teamService.createTeam(team);

        return "redirect:/manager_team";
    }

    @GetMapping("/change_information_team")
    @PreAuthorize("hasRole('PLAYER')")
    public String changeInfoTeamGet(Model model,
      @ModelAttribute("team_user") Team teamOfUser,
      @ModelAttribute("user_current") User user
    ){
        
        Player captain = playerService.getCaptain(teamOfUser.getID(),1);


        if(!captain.getStudentID().equals(user.getID())){
            return "redirect:/manager_team";
        }
        model.addAttribute("classID_", teamOfUser.getClass1());

        model.addAttribute("allow","true");
        model.addAttribute("gender","male");

        SignUpTeam signUpTeam = new SignUpTeam();
        signUpTeam.setClass1(teamOfUser.getClass1());
        signUpTeam.setClass2(teamOfUser.getClass2());
        signUpTeam.setCaptain(captain.getStudentID());
        List<Player> players = playerService.getPlayerByTeam(teamOfUser.getID());
        signUpTeam.setPlayers(players);
        model.addAttribute("signUpTeamInfo", signUpTeam);
        model.addAttribute("title","Đăng ký đội");
        return "signupTeam";
    }

    @PostMapping("/change_information_team")
    @PreAuthorize("hasAnyRole('PLAYER')")
    public String changeInfoTeamPost(Model model,
         RedirectAttributes redirectAttributes,
         @RequestParam(value = "class1",required = false) String class1,
         @RequestParam(value = "class2",required = false) String class2,
         @RequestParam(value = "captain",required = false) String captainTeam,
         @RequestParam(value = "players",required = false) List<String> playerIDs,
         @RequestParam(value = "playerNumbers",required = false) List<Integer> playerNumbers,
         @ModelAttribute("tournament_current") Tournament tournament_current,
         @ModelAttribute("user_current") User userCurrent){

        Integer error = 0;

        if(class1 == null || captainTeam == null || playerIDs == null || playerNumbers == null){
            error = 1;
            redirectAttributes.addFlashAttribute("error", "Chưa đăng ký đội");

        }else {
            if(class1.isEmpty()){
                error = 1;
                redirectAttributes.addFlashAttribute("error", "Chưa có lớp");
            }

            if(playerIDs.isEmpty() && error == 0){
                error = 1;
                redirectAttributes.addFlashAttribute("error", "Chưa chọn cầu thủ");
            }
        }

        Integer maxPlayerInTeam = tournament_current.getMaxPlayerPerTeam();
        Integer minPlayerInTeam = tournament_current.getMinPlayerPerTeam();

        if((playerIDs.size() < minPlayerInTeam || playerIDs.size() > maxPlayerInTeam) && error == 0){
            String msg = "Thành viên phải nhiều hơn "+minPlayerInTeam.toString()+" và ít hơn "+maxPlayerInTeam.toString();
            redirectAttributes.addFlashAttribute("error", msg);
            error = 1;
        }

        if((playerNumbers.size() != playerIDs.size()) && error == 0){
            String msg = "Chọn số áo đầy đủ";
            redirectAttributes.addFlashAttribute("error", msg);
            error = 1;
        }

        if(captainTeam.isEmpty() && error == 0){
            redirectAttributes.addFlashAttribute("error", "Đội trưởng bị xóa");
            error = 1;
        }

        if(class2 != null){
            if (!class2.isEmpty()){
                Team TeamOfClass2 = teamService.getTeamByClass1OrClass2(getTournamentCurrent().getID(),class2,class2);
                if(TeamOfClass2 != null){
                    if(!TeamOfClass2.getClass1().equals(class1)){
                        redirectAttributes.addFlashAttribute("error", "Không thể thêm thành viên của đội đã đăng ký");
                        error = 1;
                    }
                }
            }
        }

        if(error == 1){
            SignUpTeam signUpTeam = new SignUpTeam();
            signUpTeam.setClass1(class1);
            if(class2 != null){
                signUpTeam.setClass2(class2);
            }
            signUpTeam.setCaptain(captainTeam);

            List<Player> players = new ArrayList<>();

            for(int i = 0; i < playerIDs.size();i++){
                Player player = new Player();
                String Id = playerIDs.get(i);
                Student student = studentService.getStudentById(Id);
                User user = userService.getUserById(Id);
                player.setStudentID(student.getID());
                player.setTournament(getTournamentCurrent());
                player.setName(user.getName());
                player.setClassID(student.getClassID());
                player.setPlayerNumber(playerNumbers.get(i));
                players.add(player);
            }

            signUpTeam.setPlayers(players);
            redirectAttributes.addFlashAttribute("signUpTeamInfo", signUpTeam);
            return "redirect:/change_information_team";
        }

        Team team;
        if(class2 == null){
            team = teamService.getTeamByClass1OrClass2(tournament_current.getID(),class1,class1);
        }else{
            if (class2.isEmpty()){
                team = teamService.getTeamByClass1OrClass2(tournament_current.getID(),class1,class1);
            }else {
                team = teamService.getTeamByClass1OrClass2(tournament_current.getID(),class1,class2);
                team.setClass2(class2);
            }
        }

        Student studentCaptain = studentService.getStudentById(captainTeam);

        if(studentCaptain.getClassID().equals(class2)){
            String team1Tam = team.getClass1();
            team.setClass1(class2);
            team.setClass2(team1Tam);
        }
        team.setTotalPlayer(playerIDs.size());

        List<Player> players = playerService.getPlayerByTeam(team.getID());
        if(players != null){
            for(Player player: players){
                Account account = accountService.getAccountById(player.getStudentID());
                account.setRole("ROLE_STUDENT");
                accountService.save(account);
            }
            playerService.deletePlayers(players);
        }

        Integer checkStudentClass1 = 0, checkStudentClass2 = 0;

        for(int i = 0; i < playerIDs.size(); i++){
            Student student = studentService.getStudentById(playerIDs.get(i));
            User user = userService.getUserById(playerIDs.get(i));
            Account account = accountService.getAccountById(playerIDs.get(i));
            account.setRole("ROLE_PLAYER");
            accountService.save(account);
            Player player = new Player();
            player.setStudentID(student.getID());
            player.setTournament(getTournamentCurrent());
            player.setName(user.getName());
            player.setClassID(student.getClassID());
            if(class1.equals(student.getClassID())){
                checkStudentClass1 = 1;
            }
            if(class2 != null){
                if(class2.equals(student.getClassID())){
                    checkStudentClass2 = 1;
                }
            }
            player.setPlayerNumber(playerNumbers.get(i));
            player.setTeam(team);
            player.setStatus("signup");
            player.setGoal(0);
            player.setAssists(0);
            player.setYellowCard(0);
            player.setRedCard(0);
            player.setHeight(0);
            player.setWeight(0);
            if(playerIDs.get(i).equals(captainTeam)){
                player.setCaptain(1);
            }
            else {
                player.setCaptain(0);
            }
            playerService.createPlayer(player);
        }

        if(checkStudentClass1 == 0){
            String class2Tam = team.getClass2();
            team.setClass1(class2Tam);
            team.setClass2("");
        }
        if(checkStudentClass2 == 0){
            team.setClass2("");
        }
        teamService.createTeam(team);

        return "redirect:/manager_team";
    }

    // player view team
    @GetMapping("/manager_team")
    public String teamInfoPlayer(Model model,
         @ModelAttribute("team_user") Team team,
         @ModelAttribute("user_role") String userRole){

        if(userRole.equals("ROLE_STUDENT")){
            return "redirect:/";
        }

        if(team == null){
            return "redirect:/";
        }

        List<RoundSchedule> roundScheduleListInprogress = roundScheduleService.getRoundScheduleByStatus(getTournamentCurrent().getID(), "inprogress");
        if(!roundScheduleListInprogress.isEmpty()) {
            RoundSchedule roundScheduleInprogress = roundScheduleListInprogress.get(0);
            if (roundScheduleInprogress != null) {
                model.addAttribute("roundScheduleInprogress", roundScheduleInprogress);
                if (roundScheduleInprogress.getRoundTitle().equals("group-stage")) {
                    String groupTitle = team.getGroupStage().substring(0, 1);
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), "group-stage-" + groupTitle, team.getID());
                    if (matchListIngame.size() > 0)
                        model.addAttribute("matchListIngame", matchListIngame);
                } else {
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), roundScheduleInprogress.getRoundTitle(), team.getID());
                    if (matchListIngame.size() > 0)
                        model.addAttribute("matchListIngame", matchListIngame);
                }
            }
        }
        List<RoundSchedule> roundScheduleDone = roundScheduleService.getRoundScheduleByStatus(getTournamentCurrent().getID(), "done");
        for(RoundSchedule roundSchedule: roundScheduleDone){
            if(roundSchedule.getRoundTitle().equals("group-stage")){
                if(!team.getGroupStage().isEmpty()) {
                    String groupTitle = team.getGroupStage().substring(0, 1);
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), "group-stage-" + groupTitle, team.getID());
                    if (matchListIngame.size() > 0)
                        model.addAttribute("group_stage_done", matchListIngame);
                }
            }else{
                List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), roundSchedule.getRoundTitle(),team.getID());
                if(matchListIngame.size() > 0)
                    model.addAttribute(roundSchedule.getRoundTitle().replace("-","_")+"_done",matchListIngame);
            }
        }


        List<Player> topScore = playerService.getPlayersByTeamIDAndScoreDesc(getTournamentCurrent().getID(),team.getID());
        List<Player> topAssist = playerService.getPlayersByTeamIDAndAssistsDesc(getTournamentCurrent().getID(),team.getID());
        List<Player> topRedCard = playerService.getPlayersTeamIDAndByRedCardDesc(getTournamentCurrent().getID(),team.getID());
        List<Player> topYellowCard = playerService.getPlayersByTeamIDAndYellowCardDesc(getTournamentCurrent().getID(),team.getID());

        List<PlayerStats> topScorePlayers = new ArrayList<>();
        for(Player player_ : topScore){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getGoal());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topScorePlayers.add(player_Stats);
        }

        List<PlayerStats> topAssistPlayers = new ArrayList<>();
        for(Player player_ : topAssist){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getAssists());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topAssistPlayers.add(player_Stats);
        }

        List<PlayerStats> topRedCardPlayers = new ArrayList<>();
        for(Player player_ : topRedCard){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getRedCard());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topRedCardPlayers.add(player_Stats);
        }

        List<PlayerStats> topYellowCardPlayers = new ArrayList<>();
        for(Player player_ : topYellowCard){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getYellowCard());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topYellowCardPlayers.add(player_Stats);
        }
        model.addAttribute("topScore",topScorePlayers);
        model.addAttribute("topAssist",topAssistPlayers);
        model.addAttribute("topRedCard",topRedCardPlayers);
        model.addAttribute("topYellowCard",topYellowCardPlayers);

        Player captain = playerService.getCaptain(team.getID(),1);
        model.addAttribute("captain",captain);

        List<Player> players = playerService.getPlayerByTeam(team.getID());
        model.addAttribute("players",players);

        model.addAttribute("title","Thông tin đội bóng");
        return "teamInfoPlayer";
    }

    @GetMapping("/list_team")
    public String listTeam(Model model,
       @RequestParam(name = "teamID",required = false) String teamID,
       @RequestParam(name = "teamStatus",required = false) String status,
       @ModelAttribute("tournament_current") Tournament tournament
       ){
        if(tournament.getStatus().equals("open")){
            List<Team> teams = teamService.getTeamsByTeamIDAndStatus(tournament.getID(),teamID,status);
            model.addAttribute("teams",teams);
        }else {
            if(status != null){
                List<Team> teams = teamService.getTeamsByTeamIDAndStatus(tournament.getID(),teamID,status);
                model.addAttribute("teams", teams);
            }
            else {
                List<Team> teams = teamService.getTeamsByTeamIDAndStatus(tournament.getID(),teamID,"join");
                model.addAttribute("teams",teams);
            }
        }

        model.addAttribute("title","Danh sách đội");
        return "confirmTeam";
    }

    @GetMapping("/team/{teamID}")
    public String teamInfo(Model model,
    @PathVariable(name = "teamID") String teamID,
    @ModelAttribute("player_user") Player player,
    @ModelAttribute("user_role") String role
    ){
        if(teamID == null){
            return "redirect:/list_team";
        }

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if(teamID.isEmpty() || !pattern.matcher(teamID).matches()){
            return "redirect:/list_team";
        }

        if(player != null){
            if(player.getTeam().getID() == Integer.parseInt(teamID)){
                return "redirect:/manager_team";
            }
        }


        Team team = teamService.getTeamById(Integer.parseInt(teamID));
        if(team == null){
            return "redirect:/list_team";
        }
        model.addAttribute("team",team);

        List<RoundSchedule> roundScheduleListInprogress = roundScheduleService.getRoundScheduleByStatus(getTournamentCurrent().getID(), "inprogress");

        if(!roundScheduleListInprogress.isEmpty()) {
            RoundSchedule roundScheduleInprogress = roundScheduleListInprogress.get(0);
            if (roundScheduleInprogress != null) {
                model.addAttribute("roundScheduleInprogress", roundScheduleInprogress);
                if (roundScheduleInprogress.getRoundTitle().equals("group-stage")) {
                    String groupTitle = team.getGroupStage().substring(0, 1);
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), "group-stage-" + groupTitle, team.getID());
                    if (matchListIngame.size() > 0) {
                        model.addAttribute("matchListIngame", matchListIngame);
                    }
                } else if (!roundScheduleInprogress.getRoundTitle().equals("final")) {
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), roundScheduleInprogress.getRoundTitle(), team.getID());
                    if (matchListIngame.size() > 0) {
                        model.addAttribute("matchListIngame", matchListIngame);
                    }
                } else {
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), "third-place", team.getID());
                    matchListIngame.addAll(matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), "final", team.getID()));
                    if (matchListIngame.size() > 0) {
                        if (matchListIngame.get(0).getType().equals("third-place")) {
                            model.addAttribute("final_type", "Tranh hạng ba");
                        } else {
                            model.addAttribute("final_type", "Chung kết");
                        }
                        model.addAttribute("matchListIngame", matchListIngame);
                    }
                }
            }
        }
        List<RoundSchedule> roundScheduleDone = roundScheduleService.getRoundScheduleByStatus(getTournamentCurrent().getID(), "done");
            for(RoundSchedule roundSchedule: roundScheduleDone){
                if(roundSchedule.getRoundTitle().equals("group-stage")){
                    if(!team.getGroupStage().isEmpty()){
                        String groupTitle = team.getGroupStage().substring(0,1);
                        List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), "group-stage-"+groupTitle,team.getID());
                        if(matchListIngame.size() > 0){
                            model.addAttribute("group_stage_done",matchListIngame);
                        }
                    }
                }else if(!roundSchedule.getRoundTitle().equals("final")){
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), roundSchedule.getRoundTitle(),team.getID());
                    if(matchListIngame.size() > 0){
                        model.addAttribute(roundSchedule.getRoundTitle().replace("-","_")+"_done",matchListIngame);
                    }
                }else{
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), "third-place",team.getID());
                    matchListIngame.addAll(matchService.getMatchByTypeAndTeamID(getTournamentCurrent().getID(), "final",team.getID()));
                    if(matchListIngame.size() > 0){
                        if(matchListIngame.get(0).getType().equals("third-place")){
                            model.addAttribute("final_type","Tranh hạng ba");
                        }else{
                            model.addAttribute("final_type","Chung kết");
                        }
                        model.addAttribute(roundSchedule.getRoundTitle().replace("-","_")+"_done",matchListIngame);
                    }
                }
            }

        Player captain = playerService.getCaptain(team.getID(),1);
        model.addAttribute("captain",captain);

        List<Player> players = playerService.getPlayerByTeam(team.getID());
        model.addAttribute("players",players);

        List<Player> topScore = playerService.getPlayersByTeamIDAndScoreDesc(getTournamentCurrent().getID(),team.getID());
        List<Player> topAssist = playerService.getPlayersByTeamIDAndAssistsDesc(getTournamentCurrent().getID(),team.getID());
        List<Player> topRedCard = playerService.getPlayersTeamIDAndByRedCardDesc(getTournamentCurrent().getID(),team.getID());
        List<Player> topYellowCard = playerService.getPlayersByTeamIDAndYellowCardDesc(getTournamentCurrent().getID(),team.getID());

        List<PlayerStats> topScorePlayers = new ArrayList<>();
        for(Player player_ : topScore){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getGoal());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topScorePlayers.add(player_Stats);
        }

        List<PlayerStats> topAssistPlayers = new ArrayList<>();
        for(Player player_ : topAssist){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getAssists());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topAssistPlayers.add(player_Stats);
        }

        List<PlayerStats> topRedCardPlayers = new ArrayList<>();
        for(Player player_ : topRedCard){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getRedCard());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topRedCardPlayers.add(player_Stats);
        }

        List<PlayerStats> topYellowCardPlayers = new ArrayList<>();
        for(Player player_ : topYellowCard){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getYellowCard());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topYellowCardPlayers.add(player_Stats);
        }
        model.addAttribute("topScore",topScorePlayers);
        model.addAttribute("topAssist",topAssistPlayers);
        model.addAttribute("topRedCard",topRedCardPlayers);
        model.addAttribute("topYellowCard",topYellowCardPlayers);


        model.addAttribute("title","Thông tin đội bóng");

        if(role != null){
            if(role.equals("ROLE_MANAGER")){
                return "teamInfoManager";
            }
        }

        return "teamInfoUser";

    }


//    @GetMapping("/create_schedule")
//    @PreAuthorize("hasRole('MANAGER')")
//    public String teamInfo(Model model,@ModelAttribute("tournament_current") Tournament tournament){
//        model.addAttribute("title","Bốc thăm, xếp lịch");
//        return "createSchedule";
//    }
//
//    @PostMapping("/create_schedule")
//    @PreAuthorize("hasRole('MANAGER')")
//    public String teamInfoSubmit(
//            @ModelAttribute("tournament_current") Tournament tournament,
//            @RequestParam(value = "timeHalf",required = true) String timeHalf,
//            @RequestParam(value = "breakTime",required = true) String breakTime,
//            @RequestParam(value = "breakTimeTeam",required = true) String breakTimeTeam,
//            @RequestParam(value = "fStageStartDate",required = true) LocalDate fStageStartDate,
//            @RequestParam(value = "fStageEndDate",required = true) LocalDate fStageEndDate,
//            @RequestParam(value = "fStageMatchSameTime",required = true) String fStageMatchSameTime,
//            @RequestParam(value = "break2Match",required = true) String break2Match,
//            @RequestParam(value = "morningStart",required = true) String morningStart,
//            @RequestParam(value = "morningEnd",required = true) String morningEnd,
//            @RequestParam(value = "eveningStart",required = true) String eveningStart,
//            @RequestParam(value = "eveningEnd",required = true) String eveningEnd,
//            Model model){
//
//        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("H:mm");
//        LocalTime timeMorningStart = LocalTime.parse(morningStart, formatter);
//        LocalTime timeMorningEnd = LocalTime.parse(morningEnd, formatter);
//        LocalTime timeEveningStart = LocalTime.parse(eveningStart, formatter);
//        LocalTime timeEveningEnd = LocalTime.parse(eveningEnd, formatter);
//
//        tournament.setFStageTimeMorningStart(timeMorningStart);
//        tournament.setFStageTimeMorningEnd(timeMorningEnd);
//        tournament.setFStageTimeEveningStart(timeEveningStart);
//        tournament.setFStageTimeEveningEnd(timeEveningEnd);
//
//        Integer timeHalf_ = Integer.parseInt(timeHalf);
//        Integer breakTime_ = Integer.parseInt(breakTime);
//        Integer breakTimeTeam_ = Integer.parseInt(breakTimeTeam);
//        Integer fStageMatchSameTime_ = Integer.parseInt(fStageMatchSameTime);
//        Integer break2Match_ = Integer.parseInt(break2Match);
//
//        tournament.setTimeHalf(timeHalf_);
//        tournament.setTimeBreak(breakTime_);
//        tournament.setBreakTimeTeam(breakTimeTeam_);
//        tournament.setBreak2Match(break2Match_);
//        tournament.setFStageMatchSameTime(fStageMatchSameTime_);
//        tournament.setFStageStartDate(fStageStartDate);
//        tournament.setFStageEndDate(fStageEndDate);
//        tournamentService.createTournament(tournament);
//
//
////            String nameProcess = "createSchedule";
////            if(uploadProcessService.canThread(nameProcess)) {
////                uploadProcessService.setThreadStart(nameProcess);
////                try{
////                    test.testThread().thenRun(() ->
////                            uploadProcessService.setThreadFinish(nameProcess)
////                    );
////                }catch (Exception e) {
////                    uploadProcessService.setThreadFinish(nameProcess);
////                    redirectAttributes.addFlashAttribute("threadError", "Đang tạo lịch");
////                }
////            } else {
////                redirectAttributes.addFlashAttribute("threadError", "Đang tạo lịch");
////                return "redirect:/create_schedule";
////            }
//
////        drawMatchService.createDraw();
//
//        return "redirect:/";
//    }


    @GetMapping("/create_draw")
    @PreAuthorize("hasRole('MANAGER')")
    public String createDraw(Model model,
     @ModelAttribute("tournament_current") Tournament tournament,@ModelAttribute("roundSchedule_inprogress") RoundSchedule roundSchedule){

//        if(roundSchedule != null){
//            return "redirect:/";
//        }

        String nameProcess = "createDraw";
        if(!uploadProcessService.canThread(nameProcess)){
            model.addAttribute("createDrawError","Đang bốc thăm");
        }
        List<RoundSchedule> roundSchedules = roundScheduleService.getAllRoundSchedule(tournament.getID());
        model.addAttribute("roundSchedules",roundSchedules);

        List<Team> teams = teamService.getTeamsByStatus(tournament.getID(),"join");
        Integer teamSize = teams.size();
        model.addAttribute("teamSize",teamSize);
        model.addAttribute("title","Bốc thăm");
        return "createDraw";
    }

    @PostMapping("/create_draw")
    @PreAuthorize("hasRole('MANAGER')")
    public String createDraw(Model model,RedirectAttributes redirectAttributes){
        String nameProcess = "createDraw";
        if(uploadProcessService.canThread(nameProcess)) {
            uploadProcessService.setThreadStart(nameProcess);
            try{
                drawMatchService.createDraw().thenRun(()->
                    uploadProcessService.setThreadFinish(nameProcess)
                );
            }catch (Exception e) {
                uploadProcessService.setThreadFinish(nameProcess);
                redirectAttributes.addFlashAttribute("createDrawError", "Đang bốc thăm");
            }
        } else {
            redirectAttributes.addFlashAttribute("createDrawError", "Đang bốc thăm");
            return "redirect:/create_draw";
        }

//        drawMatchService.createDraw();
        return "redirect:/create_draw";
    }

    @GetMapping("/create_schedule")
    @PreAuthorize("hasRole('MANAGER')")
    public String directCreateSchedule(Model model,
           @ModelAttribute("roundSchedule_inprogress") RoundSchedule roundSchedule){
        if(roundSchedule != null){
            return "redirect:/create_schedule/"+roundSchedule.getRoundTitle();
        }
        return "redirect:/create_draw";
    }

    @GetMapping("/create_schedule/{round}")
    @PreAuthorize("hasRole('MANAGER')")
    public String createSchedule(Model model,
        @PathVariable(name = "round") String round,
        @RequestParam(value = "matchTime",required = false) String matchTime,
        @RequestParam(value = "matchDate",required = false) String matchDate)
    {

        if(getTournamentCurrent() == null){
            return "redirect:/";
        }

        if(round == null){
            return "redirect:/";
        }

        if (round.isEmpty()){
            return "redirect:/";
        }

        RoundSchedule roundSchedule = null;
        if(round.contains("group-stage")){
            roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(),"group-stage");
        }else {
            roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(),round);
        }


        if(roundSchedule.getScheduleStatus().equals("notyet")){
            return "redirect:/";
        }

        String nameProcess = "createSchedule";
        if(!uploadProcessService.canThread(nameProcess)){
            model.addAttribute("createSchedule","Đang xếp lịch");
        }

        LocalDate date = null;
        LocalTime time = null;

        if(matchDate != null){
            if (!matchDate.isEmpty()) {
                date = LocalDate.parse(matchDate);
            }
        }

        if(matchTime != null){
            if (!matchTime.isEmpty()) {
                time = LocalTime.parse(matchTime);
            }
        }

        if(round.contains("group-stage")){
            List<Match_> matchList = matchService.getMatchesByTypeAndStatusAndDateAndTimeForGroupStage(getTournamentCurrent().getID(), round,"ingame",date,time);
            List<Match_> matchList_done = matchService.getMatchesByTypeAndStatusAndDateAndTimeForGroupStage(getTournamentCurrent().getID(), round,"done",date,time);
            List<Match_> matchList_playing = matchService.getMatchesByTypeAndStatusAndDateAndTimeForGroupStage(getTournamentCurrent().getID(), round,"playing",date,time);
            matchList.addAll(matchList_done);
            matchList.addAll(matchList_playing);
            if(matchList_done.size() > 0){
                model.addAttribute("check",matchList);
            }
            model.addAttribute("matchList",matchList);
        }else{

            List<Match_> matchList;
            List<Match_> matchList_done;
            List<Match_> matchList_playing;
            if(round.equals("final")){
                matchList = matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), "final","ingame",date,time);
                matchList.addAll(matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), "third-place","ingame",date,time));
                matchList_done = matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), "final","done",date,time);
                matchList_done.addAll(matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), "third-place","done",date,time));
                matchList_playing = matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), "final","playing",date,time);
                matchList_playing.addAll(matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), "third-place","playing",date,time));
            }else{
                matchList = matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), round,"ingame",date,time);
                matchList_done = matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), round,"done",date,time);
                matchList_playing = matchService.getMatchesByTypeAndStatusAndDateAndTime(getTournamentCurrent().getID(), round,"playing",date,time);
            }

            matchList.addAll(matchList_done);
            matchList.addAll(matchList_playing);

            if(matchList_done.size() > 0){
                model.addAttribute("check",matchList);
            }
            model.addAttribute("matchList",matchList);
        }

        model.addAttribute("roundSchedule",roundSchedule);
        List<RoundSchedule> roundSchedules = roundScheduleService.getAllRoundSchedule(getTournamentCurrent().getID());
        model.addAttribute("roundSchedules",roundSchedules);

        if(roundSchedule.getError() != null){
            if(roundSchedule.getError() > 0){
                List<ErrorMatchSubject> errorMatchSubjects = errorMatchSubjectService.getByTournamentIDAndRoundTitle(getTournamentCurrent().getID(), round);
                List<ErrorMatchSameTime> errorMatchSameTimes = errorMatchSameTimeService.getByTournamentIDAndRoundTitle(getTournamentCurrent().getID(), round);
                List<ErrorMatchBreak> errorMatchBreaks = errorMatchBreakService.getByTournamentIDAndRoundTitle(getTournamentCurrent().getID(), round);
                boolean hasErrors = errorMatchSubjects != null || errorMatchBreaks != null || errorMatchSameTimes != null;
                model.addAttribute("hasErrors", hasErrors);
                model.addAttribute("errorMatchSubjects",errorMatchSubjects);
                model.addAttribute("errorMatchSameTimes",errorMatchSameTimes);
                model.addAttribute("errorMatchBreaks",errorMatchBreaks);
            }

            int time1Match = getTournamentCurrent().getTimeHalf() * 2 + getTournamentCurrent().getTimeBreak();
            LocalTime timeStartMorning = roundSchedule.getTimeMorningStart();
            LocalTime timeEndMorning = roundSchedule.getTimeMorningEnd();
            LocalTime timeStartEvening = roundSchedule.getTimeEveningStart();
            LocalTime timeEndEvening = roundSchedule.getTimeEveningEnd();
            LocalTime currentTime = timeStartMorning;

            List<LocalTime> timeMorningMatchs = new ArrayList<>();
            List<LocalTime> timeEveningMatchs = new ArrayList<>();
            while (currentTime.plusMinutes(time1Match).isBefore(timeEndMorning.plusMinutes(getTournamentCurrent().getTimeHalf()))||
                    currentTime.plusMinutes(time1Match).equals(timeEndMorning.plusMinutes(getTournamentCurrent().getTimeHalf()))){
                timeMorningMatchs.add(currentTime);
                currentTime = currentTime.plusMinutes(time1Match + 15);
            }

            currentTime = timeStartEvening;
            while (currentTime.plusMinutes(time1Match).isBefore(timeEndEvening.plusMinutes(getTournamentCurrent().getTimeHalf()))||
                    currentTime.plusMinutes(time1Match).equals(timeEndEvening.plusMinutes(getTournamentCurrent().getTimeHalf()))){
                timeEveningMatchs.add(currentTime);
                currentTime = currentTime.plusMinutes(time1Match + 15);
            }

            model.addAttribute("timeMorningMatchs",timeMorningMatchs);
            model.addAttribute("timeEveningMatchs",timeEveningMatchs);
        }

        model.addAttribute("title","Xếp lịch");
        return "createScheduleMatch";
    }

    @PostMapping("/create_schedule")
    @PreAuthorize("hasRole('MANAGER')")
    public String createSchedule(
     RedirectAttributes redirectAttributes,
     @Valid @ModelAttribute("roundSchedule") RoundSchedule roundSchedule,
     @RequestParam(value = "timeHalf") String timeHalf,
     @RequestParam(value = "breakTime") String breakTime,
     @RequestParam(value = "breakTimeTeam") String breakTimeTeam,
     @RequestParam(value = "matchSameTime") String matchSameTime,
     BindingResult result){

        if(result.hasErrors()){
            return "redirect:/create_schedule/"+roundSchedule.getRoundTitle();
        }

        Tournament tournament = getTournamentCurrent();

        tournament.setTimeBreak(Integer.parseInt(breakTime));
        tournament.setTimeHalf(Integer.parseInt(timeHalf));
        tournament.setBreakTimeTeam(Integer.parseInt(breakTimeTeam));
        tournament.setMatchSameTime(Integer.parseInt(matchSameTime));

        tournamentService.createTournament(tournament);

        roundScheduleService.createRoundSchedule(roundSchedule);

        String nameProcess = "createSchedule";
        if(uploadProcessService.canThread(nameProcess)) {
            uploadProcessService.setThreadStart(nameProcess);
            try{
                geneticAlgorithmService.GeneticAlgorithm().thenRun(()->
                        uploadProcessService.setThreadFinish(nameProcess)
                );
            }catch (Exception e) {
                uploadProcessService.setThreadFinish(nameProcess);
                redirectAttributes.addFlashAttribute("createSchedule", "Đang xếp lịch");
            }
        } else {
            redirectAttributes.addFlashAttribute("createSchedule", "Đang xếp lịch");
            return "redirect:/create_schedule/"+roundSchedule.getRoundTitle();
        }

        return "redirect:/create_schedule/"+roundSchedule.getRoundTitle();
    }
    @GetMapping("/match_direct/{matchID}")
    public String matchDirect(Model model,
          @PathVariable(name = "matchID") String matchID,
          @ModelAttribute("user_role") String role,
          RedirectAttributes redirectAttributes){
        return "redirect:/";
    }

    @GetMapping("/match/{matchID}")
    public String matchInfo(
            Model model,
            @ModelAttribute("user_role") String role,
            @ModelAttribute("player_user") Player player,
            @PathVariable(name = "matchID") String matchID){

        if (getTournamentCurrent() == null) {
            return "redirect:/";
        }

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");

        int matchIDTrans = -1;

        if(matchID != null ){
            if(pattern.matcher(matchID).matches()){
                matchIDTrans = Integer.parseInt(matchID);
            }
        }else{
            return "redirect:/";
        }

        if(matchIDTrans >= 0){
            Match_ match_ = matchService.getMatchById(matchIDTrans);
            RoundSchedule roundSchedule = null;
            if(match_.getType().contains("group-stage")){
                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(), "group-stage");
            }else if(!match_.getType().equals("third-place")){
                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(), match_.getType());
            }else if(match_.getType().equals("third-place")){
                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(getTournamentCurrent().getID(), "final");
                model.addAttribute("third_place","Tranh hạng ba");
            }

            model.addAttribute("roundSchedule",roundSchedule);
            model.addAttribute("match",match_);
            model.addAttribute("team1ID",match_.getTeam1().getID());
            model.addAttribute("team2ID",match_.getTeam2().getID());

            if(role != null){
                if(role.equals("ROLE_PLAYER") && player.getCaptain() == 1 && (player.getTeam().getID() == match_.getTeam1().getID() || player.getTeam().getID() == match_.getTeam2().getID())){
                    List<Player> playerList = playerService.getPlayersByTeamIDAndStatus(player.getTeam().getID(),"play");
                    model.addAttribute("playerList",playerList);
                }
            }

            if(match_ == null || roundSchedule == null){
                return "redirect:/";
            }

            List<Attendance> playerMainTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(),match_.getTeam1().getID(),"main");
            List<Player> playersMainTeam1 = playerMainTeam1.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            model.addAttribute("playersMainTeam1",playersMainTeam1);
            List<Attendance> playerSubTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(),match_.getTeam1().getID(),"sub");
            List<Player> playersSubTeam1 = playerSubTeam1.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            model.addAttribute("playersSubTeam1",playersSubTeam1);
            List<Attendance> playerBannedTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(),match_.getTeam1().getID(),"banned");
            List<Player> playersBannedTeam1 = playerBannedTeam1.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            List<String> reasonBannedTeam1 = playerBannedTeam1.stream().map(Attendance::getReason).collect(Collectors.toList());
            model.addAttribute("playersBannedTeam1",playersBannedTeam1);
            model.addAttribute("reasonBannedTeam1",reasonBannedTeam1);


            List<Attendance> playerMainTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(),match_.getTeam2().getID(),"main");
            List<Player> playersMainTeam2 = playerMainTeam2.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            model.addAttribute("playersMainTeam2",playersMainTeam2);
            List<Attendance> playerSubTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(),match_.getTeam2().getID(),"sub");
            List<Player> playersSubTeam2 = playerSubTeam2.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            model.addAttribute("playersSubTeam2",playersSubTeam2);

            List<Attendance> playerBannedTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(),match_.getTeam2().getID(),"banned");
            List<Player> playersBannedTeam2 = playerBannedTeam2.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            List<String> reasonBannedTeam2 = playerBannedTeam2.stream().map(Attendance::getReason).collect(Collectors.toList());
            model.addAttribute("playersBannedTeam2",playersBannedTeam2);
            model.addAttribute("reasonBannedTeam2",reasonBannedTeam2);

            if(playerMainTeam1.size() > 0 ){
                List<Player> playerAbsentTeam1 = playerService.getPlayerByTeam(match_.getTeam1().getID());
                playerAbsentTeam1.removeAll(playersMainTeam1);
                playerAbsentTeam1.removeAll(playersSubTeam1);
                playerAbsentTeam1.removeAll(playersBannedTeam1);
                model.addAttribute("playerAbsentTeam1",playerAbsentTeam1);

            }

            if(playerMainTeam2.size() > 0 ){
                List<Player> playerAbsentTeam2 = playerService.getPlayerByTeam(match_.getTeam2().getID());
                playerAbsentTeam2.removeAll(playersMainTeam2);
                playerAbsentTeam2.removeAll(playersSubTeam2);
                playerAbsentTeam2.removeAll(playersBannedTeam2);
                model.addAttribute("playerAbsentTeam2",playerAbsentTeam2);
            }


            String team1Title = !match_.getTeam1().getClass2().isEmpty() ? match_.getTeam1().getClass1()+" - "+match_.getTeam1().getClass2() : match_.getTeam1().getClass1();
            String team2Title = !match_.getTeam2().getClass2().isEmpty() ? match_.getTeam2().getClass1()+" - "+match_.getTeam2().getClass2() : match_.getTeam2().getClass1();
            model.addAttribute("team1Title",team1Title);
            model.addAttribute("team2Title",team2Title);


            List<Livescore> livescoresTeam1 = livescoreService.getLivescoreByMatchIDAndTeamID(matchIDTrans,match_.getTeam1().getID());
            model.addAttribute("livescoresTeam1",livescoresTeam1);

            List<Livescore> livescoresTeam2 = livescoreService.getLivescoreByMatchIDAndTeamID(matchIDTrans,match_.getTeam2().getID());
            model.addAttribute("livescoresTeam2",livescoresTeam2);

            List<LivePenalty> livePenaltiesTeam1 = livePenaltyService.getLivePenaltyByMatchIDAndTeamID(matchIDTrans,match_.getTeam1().getID());
            model.addAttribute("livePenaltiesTeam1",livePenaltiesTeam1);

            List<LivePenalty> livePenaltiesTeam2 = livePenaltyService.getLivePenaltyByMatchIDAndTeamID(matchIDTrans,match_.getTeam2().getID());
            model.addAttribute("livePenaltiesTeam2",livePenaltiesTeam2);

            List<LiveCard> liveCardsTeam1 = liveCardService.getLiveCardByMatchIDAndTeamID(matchIDTrans,match_.getTeam1().getID());
            model.addAttribute("liveCardsTeam1",liveCardsTeam1);

            List<LiveCard> liveCardsTeam2 = liveCardService.getLiveCardByMatchIDAndTeamID(matchIDTrans,match_.getTeam2().getID());
            model.addAttribute("liveCardsTeam2",liveCardsTeam2);


            if(playerMainTeam1.size() < 5 || playerMainTeam2.size() < 5){
                model.addAttribute("start",false);
            }else{
                model.addAttribute("start",true);
            }


        }else{
            return "redirect:/";
        }

        model.addAttribute("title","Thông tin trận");
        return "matchInfo";
    }

    @PostMapping("/attendance")
    @PreAuthorize("hasRole('PLAYER')")
    public String matchAttendance(Model model,
       @RequestParam(value = "matchID") String matchID,
       @RequestParam(value = "teamID") String teamID,
       @RequestParam(value = "attendanceList") List<String> attendanceList,
       @RequestParam(value = "mainPlayers") List<String> mainPlayers,
       @RequestParam(value = "bannedList", required = false) List<String> bannedList){

        if(teamID == null ||
            matchID == null ||
            mainPlayers == null ||
            attendanceList == null){
            return "redirect:/";
        }

        int matchIDTrans = -1;
        int teamIDTrans = -1;

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if(pattern.matcher(matchID).matches()){
            matchIDTrans = Integer.parseInt(matchID);
        }else {
            return "redirect:/";
        }

        if(pattern.matcher(teamID).matches()){
            teamIDTrans = Integer.parseInt(teamID);
        }else {
            return "redirect:/";
        }

        if(matchIDTrans < 0 || teamIDTrans < 0){
            return "redirect:/";
        }

        List<Integer> mainPlayers_ = mainPlayers.stream()
                .map(Integer::parseInt)
                .collect(Collectors.toList());

        Match_ match = matchService.getMatchById(matchIDTrans);
        Team team = teamService.getTeamById(teamIDTrans);

        attendanceService.deleteByMatchIDAndTeamID(match.getID(),team.getID());

        List<Attendance> attendances = new ArrayList<>();
        for (String playerID: attendanceList){
            Player player = playerService.getPlayerByID(Integer.parseInt(playerID));
            Attendance attendance = new Attendance();
            attendance.setTeam(team);
            attendance.setMatch(match);
            attendance.setPlayer(player);
            if(mainPlayers_.contains(player.getID())){
                attendance.setStatus("main");
            }else {
                attendance.setStatus("sub");
            }
            attendances.add(attendance);
        }

        if(bannedList != null){
            for (String playerID: bannedList){
                Player player = playerService.getPlayerByID(Integer.parseInt(playerID));
                Attendance attendance = new Attendance();
                attendance.setTeam(team);
                attendance.setMatch(match);
                attendance.setPlayer(player);
                attendance.setStatus("banned");
                attendance.setReason(player.getReasonBanned());
                attendances.add(attendance);
            }
        }
        attendanceService.createAllAttendance(attendances);

        return "redirect:/match/"+matchID;
    }

    @PostMapping("/start_match")
    @PreAuthorize("hasRole('MANAGER')")
    public String startMatch(Model model,
          @RequestParam(value = "matchID") String matchID){
        if(matchID == null){
            return "redirect:/";
        }

        int matchIDTrans = -1;

        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if(pattern.matcher(matchID).matches()){
            matchIDTrans = Integer.parseInt(matchID);
        }else {
            return "redirect:/";
        }

        if(matchIDTrans < 0){
            return "redirect:/";
        }

        Match_ match = matchService.getMatchById(matchIDTrans);
        if(match.getStatus().equals("ingame")){
            match.setStatus("playing");
            matchService.createMatch(match);
        }

        return "redirect:/match/"+matchID;
    }

    @GetMapping("{year}/team/{teamID}")
    public String teamInfoRequired(Model model,
       @PathVariable(name = "teamID") String teamID,
       @PathVariable(name = "year") String year
    ){
        if(teamID == null){
            return "redirect:/list_team";
        }

        Pattern pattern1 = Pattern.compile("-?\\d+(\\.\\d+)?");
        Pattern pattern2 = Pattern.compile("^(\\d{4})-(\\d{4})$");
        SchoolYear schoolYear = null;
        Tournament tournament = null;

        if(pattern2.matcher(year).matches()){
            Integer year1 = Integer.parseInt(year.split("-")[0]);
            Integer year2 = Integer.parseInt(year.split("-")[1]);
            schoolYear = schoolYearService.getSchoolYearByYear1AndYear2(year1,year2);
            tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());
        }

        if(schoolYear == null || tournament == null || schoolYear.getCurrent().equals("1")){
            return "redirect:/";
        }

        if(teamID.isEmpty() || !pattern1.matcher(teamID).matches()){
            return "redirect:/"+year;
        }

        Team team = teamService.getTeamById(Integer.parseInt(teamID));
        if(team == null){
            return "redirect:/"+year;
        }
        model.addAttribute("team",team);

        List<RoundSchedule> roundScheduleDone = roundScheduleService.getRoundScheduleByStatus(tournament.getID(), "done");
        for(RoundSchedule roundSchedule: roundScheduleDone){
            if(roundSchedule.getRoundTitle().equals("group-stage")){
                if(!team.getGroupStage().isEmpty()){
                    String groupTitle = team.getGroupStage().substring(0,1);
                    List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(tournament.getID(), "group-stage-"+groupTitle,team.getID());
                    if(matchListIngame.size() > 0){
                        model.addAttribute("group_stage_done",matchListIngame);
                    }
                }
            }else if(!roundSchedule.getRoundTitle().equals("final")){
                List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(tournament.getID(), roundSchedule.getRoundTitle(),team.getID());
                if(matchListIngame.size() > 0){
                    model.addAttribute(roundSchedule.getRoundTitle().replace("-","_")+"_done",matchListIngame);
                }
            }else{
                List<Match_> matchListIngame = matchService.getMatchByTypeAndTeamID(tournament.getID(), "third-place",team.getID());
                matchListIngame.addAll(matchService.getMatchByTypeAndTeamID(tournament.getID(), "final",team.getID()));
                if(matchListIngame.size() > 0){
                    if(matchListIngame.get(0).getType().equals("third-place")){
                        model.addAttribute("final_type","Tranh hạng ba");
                    }else{
                        model.addAttribute("final_type","Chung kết");
                    }
                    model.addAttribute(roundSchedule.getRoundTitle().replace("-","_")+"_done",matchListIngame);
                }
            }
        }

        Player captain = playerService.getCaptain(team.getID(),1);
        model.addAttribute("captain",captain);

        List<Player> players = playerService.getPlayerByTeam(team.getID());
        model.addAttribute("players",players);

        List<Player> topScore = playerService.getPlayersByTeamIDAndScoreDesc(tournament.getID(),team.getID());
        List<Player> topAssist = playerService.getPlayersByTeamIDAndAssistsDesc(tournament.getID(),team.getID());
        List<Player> topRedCard = playerService.getPlayersTeamIDAndByRedCardDesc(tournament.getID(),team.getID());
        List<Player> topYellowCard = playerService.getPlayersByTeamIDAndYellowCardDesc(tournament.getID(),team.getID());

        List<PlayerStats> topScorePlayers = new ArrayList<>();
        for(Player player_ : topScore){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getGoal());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topScorePlayers.add(player_Stats);
        }

        List<PlayerStats> topAssistPlayers = new ArrayList<>();
        for(Player player_ : topAssist){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getAssists());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topAssistPlayers.add(player_Stats);
        }

        List<PlayerStats> topRedCardPlayers = new ArrayList<>();
        for(Player player_ : topRedCard){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getRedCard());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topRedCardPlayers.add(player_Stats);
        }

        List<PlayerStats> topYellowCardPlayers = new ArrayList<>();
        for(Player player_ : topYellowCard){
            PlayerStats player_Stats = new PlayerStats();
            User user = userService.getUserById(player_.getStudentID());
            player_Stats.setName(user.getName());
            player_Stats.setAvatar(user.getAvatar());
            player_Stats.setPlayerID(player_.getID());
            player_Stats.setTeamID(player_.getTeam().getID());
            player_Stats.setStudentID(player_.getStudentID());
            player_Stats.setNumber(player_.getYellowCard());
            Team team_ = player_.getTeam();
            String team_Title = !team_.getClass2().isEmpty() ? team_.getClass1()+" - "+team_.getClass2() : team_.getClass1();
            player_Stats.setTeamTitle(team_Title);
            topYellowCardPlayers.add(player_Stats);
        }
        model.addAttribute("topScore",topScorePlayers);
        model.addAttribute("topAssist",topAssistPlayers);
        model.addAttribute("topRedCard",topRedCardPlayers);
        model.addAttribute("topYellowCard",topYellowCardPlayers);


        model.addAttribute("tournament_done",true);
        model.addAttribute("year",year);
        model.addAttribute("title","Thông tin đội bóng");
        return "teamInfoRequired";
    }

    @GetMapping({"/{year}", "/index/{year}","/home/{year}"})
    public String indexRequired(Model model,
        @PathVariable(name = "year") String year){
        String regex = "^(\\d{4})-(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        if(pattern.matcher(year).matches()){
            Integer year1 = Integer.parseInt(year.split("-")[0]);
            Integer year2 = Integer.parseInt(year.split("-")[1]);
            SchoolYear schoolYear = schoolYearService.getSchoolYearByYear1AndYear2(year1,year2);

            if(schoolYear == null || schoolYear.getCurrent().equals("1")){
                return "redirect:/";
            }

            Tournament tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());

            if(tournament == null){
                return "redirect:/";
            }

            List<RoundSchedule> roundSchedulesDone = roundScheduleService.getRoundScheduleByStatus(tournament.getID(), "done");

            if(roundSchedulesDone == null){
                return "redirect:/";
            }

            String roundTitle = "";
            for (RoundSchedule roundSchedule : roundSchedulesDone) {
                if (roundSchedule.getRoundTitle().equals("group-stage")) {
                    List<GroupStageMap> totalGroup = getGroupMap(roundSchedule,tournament);
                    model.addAttribute("group_stage_done", totalGroup);
                } else if (!roundSchedule.getRoundTitle().equals("final")) {
                    roundTitle = roundSchedule.getRoundTitle();
                    List<Match_> matchListIngame = matchService.getMatchByType(tournament.getID(), roundTitle);
                    model.addAttribute(roundTitle.replace("-", "_") + "_done", matchListIngame);
                } else {
                    roundTitle = roundSchedule.getRoundTitle();
                    List<Match_> matchListIngame = matchService.getMatchByType(tournament.getID(), "third-place");
                    matchListIngame.addAll(matchService.getMatchByType(tournament.getID(), "final"));
                    model.addAttribute("final_done", matchListIngame);
                }
            }


            /// thông số
            List<Player> topScore = playerService.getPlayersByScoreDesc(tournament.getID());
            List<Player> topAssist = playerService.getPlayersByAssistsDesc(tournament.getID());
            List<Player> topRedCard = playerService.getPlayersByRedCardDesc(tournament.getID());
            List<Player> topYellowCard = playerService.getPlayersByYellowCardDesc(tournament.getID());

            List<PlayerStats> topScorePlayers = new ArrayList<>();
            for (Player player : topScore) {
                PlayerStats playerStats = new PlayerStats();
                User user = userService.getUserById(player.getStudentID());
                playerStats.setName(user.getName());
                playerStats.setAvatar(user.getAvatar());
                playerStats.setTeamID(player.getTeam().getID());
                playerStats.setPlayerID(player.getID());
                playerStats.setStudentID(player.getStudentID());
                playerStats.setNumber(player.getGoal());
                Team team = player.getTeam();
                String teamTitle = !team.getClass2().isEmpty() ? team.getClass1() + " - " + team.getClass2() : team.getClass1();
                playerStats.setTeamTitle(teamTitle);
                topScorePlayers.add(playerStats);
            }

            List<PlayerStats> topAssistPlayers = new ArrayList<>();
            for (Player player : topAssist) {
                PlayerStats playerStats = new PlayerStats();
                User user = userService.getUserById(player.getStudentID());
                playerStats.setName(user.getName());
                playerStats.setAvatar(user.getAvatar());
                playerStats.setTeamID(player.getTeam().getID());
                playerStats.setPlayerID(player.getID());
                playerStats.setStudentID(player.getStudentID());
                playerStats.setNumber(player.getAssists());
                Team team = player.getTeam();
                String teamTitle = !team.getClass2().isEmpty() ? team.getClass1() + " - " + team.getClass2() : team.getClass1();
                playerStats.setTeamTitle(teamTitle);
                topAssistPlayers.add(playerStats);
            }

            List<PlayerStats> topRedCardPlayers = new ArrayList<>();
            for (Player player : topRedCard) {
                PlayerStats playerStats = new PlayerStats();
                User user = userService.getUserById(player.getStudentID());
                playerStats.setName(user.getName());
                playerStats.setAvatar(user.getAvatar());
                playerStats.setTeamID(player.getTeam().getID());
                playerStats.setPlayerID(player.getID());
                playerStats.setStudentID(player.getStudentID());
                playerStats.setNumber(player.getRedCard());
                Team team = player.getTeam();
                String teamTitle = !team.getClass2().isEmpty() ? team.getClass1() + " - " + team.getClass2() : team.getClass1();
                playerStats.setTeamTitle(teamTitle);
                topRedCardPlayers.add(playerStats);
            }

            List<PlayerStats> topYellowCardPlayers = new ArrayList<>();
            for (Player player : topYellowCard) {
                PlayerStats playerStats = new PlayerStats();
                User user = userService.getUserById(player.getStudentID());
                playerStats.setName(user.getName());
                playerStats.setTeamID(player.getTeam().getID());
                playerStats.setAvatar(user.getAvatar());
                playerStats.setPlayerID(player.getID());
                playerStats.setStudentID(player.getStudentID());
                playerStats.setNumber(player.getYellowCard());
                Team team = player.getTeam();
                String teamTitle = !team.getClass2().isEmpty() ? team.getClass1() + " - " + team.getClass2() : team.getClass1();
                playerStats.setTeamTitle(teamTitle);
                topYellowCardPlayers.add(playerStats);
            }
            model.addAttribute("topScore", topScorePlayers);
            model.addAttribute("topAssist", topAssistPlayers);
            model.addAttribute("topRedCard", topRedCardPlayers);
            model.addAttribute("topYellowCard", topYellowCardPlayers);
        }

        model.addAttribute("tournament_done",true);
        model.addAttribute("year",year);
        model.addAttribute("title","Tournament IT TDTU");
        return "indexRequired";
    }

    @GetMapping({"/{year}/user/{studentID}"})
    public String userInfoRequired(Model model,
               @PathVariable(name = "year") String year,
               @PathVariable(name = "studentID") String studentID
    ){
        String regex = "^(\\d{4})-(\\d{4})$";
        Pattern pattern = Pattern.compile(regex);
        if(pattern.matcher(year).matches()) {
            Integer year1 = Integer.parseInt(year.split("-")[0]);
            Integer year2 = Integer.parseInt(year.split("-")[1]);
            SchoolYear schoolYear = schoolYearService.getSchoolYearByYear1AndYear2(year1, year2);

            if (schoolYear == null || schoolYear.getCurrent().equals("1")) {
                return "redirect:/";
            }

            Account account = accountService.getAccountById(studentID);
            if (account != null) {
                String role = account.getRole();
                if (!role.equals("ROLE_MANAGER")) {
                    User user = userService.getUserById(studentID);
                    model.addAttribute("userInformation", user);
                    Student student = studentService.getStudentById(studentID);
                    model.addAttribute("student", student);
                    Player player = playerService.getPlayerByStudentIDAndTournamentID(studentID, getTournamentRequired(schoolYear).getID());
                    if (player != null) {
                        Team team = player.getTeam();
                        model.addAttribute("team", team);
                        model.addAttribute("player", player);
                        model.addAttribute("season", schoolYear);
                    }

                    model.addAttribute("tournament_done",true);
                    model.addAttribute("year",year);
                    model.addAttribute("title", "Thông tin cá nhân");
                    return "userInfo";
                }
            }
        }
        return "redirect:/";
    }

    @GetMapping("{year}/match/{matchID}")
    public String matchInfoRequired(
            Model model,
            @PathVariable(name = "year") String year,
            @PathVariable(name = "matchID") String matchID){

        Pattern pattern1 = Pattern.compile("-?\\d+(\\.\\d+)?");
        Pattern pattern2 = Pattern.compile("^(\\d{4})-(\\d{4})$");
        SchoolYear schoolYear = null;
        Tournament tournament = null;

        if(pattern2.matcher(year).matches()){
            Integer year1 = Integer.parseInt(year.split("-")[0]);
            Integer year2 = Integer.parseInt(year.split("-")[1]);
            schoolYear = schoolYearService.getSchoolYearByYear1AndYear2(year1,year2);
            tournament = tournamentService.getTournamentBySchoolYearID(schoolYear.getID());
        }

        if(schoolYear == null || tournament == null || schoolYear.getCurrent().equals("1")){
            return "redirect:/";
        }

        if(matchID.isEmpty() || !pattern1.matcher(matchID).matches()){
            return "redirect:/"+year;
        }

        int matchIDTrans = Integer.parseInt(matchID);

        if (matchIDTrans >= 0) {
            Match_ match_ = matchService.getMatchById(matchIDTrans);
            RoundSchedule roundSchedule = null;
            if (match_.getType().contains("group-stage")) {
                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(tournament.getID(), "group-stage");
            } else if (!match_.getType().equals("third-place")) {
                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(tournament.getID(), match_.getType());
            } else if (match_.getType().equals("third-place")) {
                roundSchedule = roundScheduleService.getRoundScheduleByRoundTitle(tournament.getID(), "final");
                model.addAttribute("third_place", "Tranh hạng ba");
            }

            model.addAttribute("roundSchedule", roundSchedule);
            model.addAttribute("match", match_);
            model.addAttribute("team1ID", match_.getTeam1().getID());
            model.addAttribute("team2ID", match_.getTeam2().getID());


            if (match_ == null || roundSchedule == null) {
                return "redirect:/";
            }

            List<Attendance> playerMainTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(), match_.getTeam1().getID(), "main");
            List<Player> playersMainTeam1 = playerMainTeam1.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            model.addAttribute("playersMainTeam1", playersMainTeam1);
            List<Attendance> playerSubTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(), match_.getTeam1().getID(), "sub");
            List<Player> playersSubTeam1 = playerSubTeam1.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            model.addAttribute("playersSubTeam1", playersSubTeam1);
            List<Attendance> playerBannedTeam1 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(), match_.getTeam1().getID(), "banned");
            List<Player> playersBannedTeam1 = playerBannedTeam1.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            List<String> reasonBannedTeam1 = playerBannedTeam1.stream().map(Attendance::getReason).collect(Collectors.toList());
            model.addAttribute("playersBannedTeam1", playersBannedTeam1);
            model.addAttribute("reasonBannedTeam1", reasonBannedTeam1);


            List<Attendance> playerMainTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(), match_.getTeam2().getID(), "main");
            List<Player> playersMainTeam2 = playerMainTeam2.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            model.addAttribute("playersMainTeam2", playersMainTeam2);
            List<Attendance> playerSubTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(), match_.getTeam2().getID(), "sub");
            List<Player> playersSubTeam2 = playerSubTeam2.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            model.addAttribute("playersSubTeam2", playersSubTeam2);

            List<Attendance> playerBannedTeam2 = attendanceService.getAttendanceByMatchIDAndTeamIDAndStatus(match_.getID(), match_.getTeam2().getID(), "banned");
            List<Player> playersBannedTeam2 = playerBannedTeam2.stream()
                    .map(Attendance::getPlayer)
                    .collect(Collectors.toList());
            List<String> reasonBannedTeam2 = playerBannedTeam2.stream().map(Attendance::getReason).collect(Collectors.toList());
            model.addAttribute("playersBannedTeam2", playersBannedTeam2);
            model.addAttribute("reasonBannedTeam2", reasonBannedTeam2);

            if (playerMainTeam1.size() > 0) {
                List<Player> playerAbsentTeam1 = playerService.getPlayerByTeam(match_.getTeam1().getID());
                playerAbsentTeam1.removeAll(playersMainTeam1);
                playerAbsentTeam1.removeAll(playersSubTeam1);
                playerAbsentTeam1.removeAll(playersBannedTeam1);
                model.addAttribute("playerAbsentTeam1", playerAbsentTeam1);

            }

            if (playerMainTeam2.size() > 0) {
                List<Player> playerAbsentTeam2 = playerService.getPlayerByTeam(match_.getTeam2().getID());
                playerAbsentTeam2.removeAll(playersMainTeam2);
                playerAbsentTeam2.removeAll(playersSubTeam2);
                playerAbsentTeam2.removeAll(playersBannedTeam2);
                model.addAttribute("playerAbsentTeam2", playerAbsentTeam2);
            }


            String team1Title = !match_.getTeam1().getClass2().isEmpty() ? match_.getTeam1().getClass1() + " - " + match_.getTeam1().getClass2() : match_.getTeam1().getClass1();
            String team2Title = !match_.getTeam2().getClass2().isEmpty() ? match_.getTeam2().getClass1() + " - " + match_.getTeam2().getClass2() : match_.getTeam2().getClass1();
            model.addAttribute("team1Title", team1Title);
            model.addAttribute("team2Title", team2Title);


            List<Livescore> livescoresTeam1 = livescoreService.getLivescoreByMatchIDAndTeamID(matchIDTrans, match_.getTeam1().getID());
            model.addAttribute("livescoresTeam1", livescoresTeam1);

            List<Livescore> livescoresTeam2 = livescoreService.getLivescoreByMatchIDAndTeamID(matchIDTrans, match_.getTeam2().getID());
            model.addAttribute("livescoresTeam2", livescoresTeam2);

            List<LivePenalty> livePenaltiesTeam1 = livePenaltyService.getLivePenaltyByMatchIDAndTeamID(matchIDTrans, match_.getTeam1().getID());
            model.addAttribute("livePenaltiesTeam1", livePenaltiesTeam1);

            List<LivePenalty> livePenaltiesTeam2 = livePenaltyService.getLivePenaltyByMatchIDAndTeamID(matchIDTrans, match_.getTeam2().getID());
            model.addAttribute("livePenaltiesTeam2", livePenaltiesTeam2);

            List<LiveCard> liveCardsTeam1 = liveCardService.getLiveCardByMatchIDAndTeamID(matchIDTrans, match_.getTeam1().getID());
            model.addAttribute("liveCardsTeam1", liveCardsTeam1);

            List<LiveCard> liveCardsTeam2 = liveCardService.getLiveCardByMatchIDAndTeamID(matchIDTrans, match_.getTeam2().getID());
            model.addAttribute("liveCardsTeam2", liveCardsTeam2);


            if (playerMainTeam1.size() < 5 || playerMainTeam2.size() < 5) {
                model.addAttribute("start", false);
            } else {
                model.addAttribute("start", true);
            }

        } else {
            return "redirect:/";
        }

        model.addAttribute("tournament_done",true);
        model.addAttribute("year",year);
        model.addAttribute("title","Thông tin trận");
        return "matchInfoRequired";
    }

}
