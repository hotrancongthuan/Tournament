package com.duan2.tournamentTDTU.genetic_algorithm;

import com.duan2.tournamentTDTU.constants.Shift;
import com.duan2.tournamentTDTU.models.*;
import com.duan2.tournamentTDTU.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.Time;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class GeneticAlgorithmService {
    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private MatchService matchService;

    @Autowired
    private SchoolYearService schoolYearService;

    @Autowired
    private RoundScheduleService roundScheduleService;

    @Autowired
    private SemesterWeekService semesterWeekService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private ScheduleStudentService scheduleStudentService;

    @Autowired
    private ScheduleSubjectService scheduleSubjectService;

    @Autowired
    private PlanYearService planYearService;

    @Value("${file.logs}")
    private String fileLogs;

    @Autowired
    private TransactionTemplate transactionTemplate;

    @Autowired
    private ErrorMatchBreakService errorMatchBreakService;
    @Autowired
    private ErrorMatchSameTimeService errorMatchSameTimeService;
    @Autowired
    private ErrorMatchSubjectService errorMatchSubjectService;

    private Shift findShift(LocalTime time){
        for (Shift shift: Shift.values()){
            LocalTime start = LocalTime.parse(shift.getStartTime(), DateTimeFormatter.ofPattern("HH:mm"));
            LocalTime end = LocalTime.parse(shift.getEndTime(),DateTimeFormatter.ofPattern("HH:mm"));
            if ((time.equals(start) || time.isAfter(start)) && time.isBefore(end)) {
                return shift;
            }
        }
        return null;
    }

    private Map<String,Double> PlayerHaveScheduleCheck(ADN adn, Team team,Map<String,Double> solutionInfo){
        int point = 0;
        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");

        Integer weekdayMatch = adn.getWeekday();
        String weekMatch = adn.getWeek();
        Integer shift = findShift(adn.getTime()).getShiftNumber();

        // danh sách môn học diễn ra trong thời gian trận đấu bắt đầu
        List<ScheduleSubject> subjectsHappenning = scheduleSubjectService.getScheduleSubjectsByShiftAndWeekdayAndWeek(schoolYear.getID(),shift,weekdayMatch,"-"+weekMatch+"-");


        // duyệt từng môn học có trong ngày thi đấu
        for(ScheduleSubject subjectHappenning: subjectsHappenning){

            // Kiểm tra trùng lịch học
            // danh sách cầu thủ đội
            List<Player> playersTeam = playerService.getPlayerByTeam(team.getID());

            // chuyển player sang studentID
            List<String> studentIDs = playersTeam.stream().map(Player::getStudentID).collect(Collectors.toList());

            // danh sách sinh viên có môn học trùng ngày và giờ thi đấu
            List<ScheduleStudent> scheduleStudents = scheduleStudentService.getScheduleStudentByScheduleSubjectIDAndStudentID(subjectHappenning.getSubjectID(),studentIDs);

            int countPlayerTeam1Schedule = scheduleStudents.size();
            int countPlayerTeam1Free = team.getTotalPlayer() - countPlayerTeam1Schedule;
            if(countPlayerTeam1Schedule == 0){
                double pointCurrent = solutionInfo.get("point") + 1000;
                solutionInfo.put("point",pointCurrent);
            } else if(countPlayerTeam1Free == 5){
                double pointCurrent = solutionInfo.get("point") + 100;
                solutionInfo.put("point",pointCurrent);
            } else if (countPlayerTeam1Free > 5) {
                double pointCurrent = solutionInfo.get("point") + 200;
                solutionInfo.put("point",pointCurrent);
            } else if (countPlayerTeam1Free < 5) {
//                System.out.println("lỗi trùng môn học: "+ team.getID()+","+subjectHappenning.getID());
                double pointCurrent = solutionInfo.get("point") - 10000;
                solutionInfo.put("point",pointCurrent);
                double errorCurrent = solutionInfo.get("error") + 1;
                solutionInfo.put("error",errorCurrent);
            }
//            System.out.println("số sinh viên trùng môn học: "+ scheduleStudents.size());
//            System.out.println("point: "+ point);
        }
        return solutionInfo;
    }

    private Map<String,Double> fitness(List<ADN> solution){
        int point = 0;

        Map<String, Double> solutionInfo = new HashMap<>();
        solutionInfo.put("point",0.0);
        solutionInfo.put("error",0.0);

        Map<Integer,LocalDate> matchTime = new HashMap<>();

        Map<String,Integer> matchSameTime = new HashMap<>();

        for(ADN adn: solution){
            Match_ match = matchService.getMatchById(adn.getMatchID());
            Team team1 = match.getTeam1();
            Team team2 = match.getTeam2();

            // kiểm tra trùng lịch học
            solutionInfo = PlayerHaveScheduleCheck(adn,team1,solutionInfo);
            solutionInfo = PlayerHaveScheduleCheck(adn,team2,solutionInfo);

//            SchoolYear schoolYear = getSchoolYearCurrent();
//            // kiểm tra trùng lịch nghỉ lễ
//            String week = adn.getWeek();
//            PlanYear planYear = planYearService.getPlanYearByWeek(schoolYear.getID(), week);
//            if(planYear != null){
//                LocalDate startPlanYear = planYear.getDate();
//                LocalDate endPlanYear = startPlanYear.plusDays(planYear.getNumDate()-1);
//                LocalDate dateMatch = adn.getDate();
//                if(dateMatch.equals(startPlanYear) || (dateMatch.isAfter(startPlanYear) && dateMatch.isBefore(endPlanYear)) || dateMatch.equals(endPlanYear)){
////                    System.out.println("lỗi trùng nghỉ lể: "+ week+","+ planYear.getName()+","+ team1.getID());
//                    double pointCurrent = solutionInfo.get("point") - 10000;
//                    solutionInfo.put("point",pointCurrent);
//                    double errorCurrent = solutionInfo.get("error") + 1;
//                    solutionInfo.put("error",errorCurrent);
//                }else {
//                    double pointCurrent = solutionInfo.get("point") + 1000;
//                    solutionInfo.put("point",pointCurrent);
//                }
////                System.out.println("ngày nghỉ trùng: "+ planYear.getName());
////                System.out.println("point: "+ point);
//            }else{
//                double pointCurrent = solutionInfo.get("point") + 1000;
//                solutionInfo.put("point",pointCurrent);
////                System.out.println("point: "+ point);
//            }

            // kiểm tra thời gian nghỉ của đội
            Tournament tournament = getTournamentCurrent();
            // ngày thi đấu
            LocalDate date = adn.getDate();
//            System.out.println("ngày thi đấu hiện tại của đội: "+date);
            if(matchTime.keySet().contains(team1.getID())){
                // ngày thi đấu trước đó của đội
                LocalDate matchTimePrev = matchTime.get(team1.getID());
//                System.out.println("ngày thi đấu trước đó của đội1: "+matchTimePrev);
                if(ChronoUnit.DAYS.between(matchTimePrev, date) == tournament.getBreakTimeTeam()){
                    // 2 ngày cách nhau theo số ngày yêu cầu
//                    System.out.println("2 ngày cách nhau theo số ngày yêu cầu");
                    double pointCurrent = solutionInfo.get("point") + 1000;
                    solutionInfo.put("point",pointCurrent);
                } else if (ChronoUnit.DAYS.between(matchTimePrev, date) > 0) {
                    // 2 ngày cách nhau từ 1 ngày trở lên
//                    System.out.println("2 ngày cách nhau từ 1 ngày trở lên");
                    double pointCurrent = solutionInfo.get("point") + 200;
                    solutionInfo.put("point",pointCurrent);
                } else if (ChronoUnit.DAYS.between(matchTimePrev, date) == 0) {
                    // 2 ngày trùng nhau
//                    System.out.println("lỗi thời gian nghỉ: "+ matchTimePrev+","+date+","+ team1.getID());
//                    System.out.println("2 ngày trùng nhau");
                    double pointCurrent = solutionInfo.get("point") - 10000;
                    solutionInfo.put("point",pointCurrent);
                    double errorCurrent = solutionInfo.get("error") + 1;
                    solutionInfo.put("error",errorCurrent);
                }
//                System.out.println("point thời gian nghỉ của đội: "+ point);
            }
            matchTime.put(team1.getID(),date);

            if(matchTime.keySet().contains(team2.getID())) {
                LocalDate matchTimePrev = matchTime.get(team2.getID());
//                System.out.println("ngày thi đấu trước đó của đội2: "+matchTimePrev);
                if (ChronoUnit.DAYS.between(matchTimePrev, date) == tournament.getBreakTimeTeam()) {
                    // 2 ngày cách theo số ngày yêu cầu
//                    System.out.println("2 ngày cách nhau theo số ngày yêu cầu");
                    double pointCurrent = solutionInfo.get("point") + 1000;
                    solutionInfo.put("point", pointCurrent);
                } else if (ChronoUnit.DAYS.between(matchTimePrev, date) > 0) {
                    // 2 ngày cách từ 1 ngày trở lên
//                    System.out.println("2 ngày cách nhau từ 1 ngày trở lên");
                    double pointCurrent = solutionInfo.get("point") + 200;
                    solutionInfo.put("point", pointCurrent);
                } else if (ChronoUnit.DAYS.between(matchTimePrev, date) == 0) {
                    // 2 ngày trùng nhau
//                    System.out.println("lỗi thời gian nghỉ đội 2: "+ matchTimePrev+","+date+","+ team2.getID());
//                    System.out.println("2 ngày trùng nhau");
                    double pointCurrent = solutionInfo.get("point") - 10000;
                    solutionInfo.put("point", pointCurrent);
                    double errorCurrent = solutionInfo.get("error") + 1;
                    solutionInfo.put("error", errorCurrent);
                }
//                System.out.println("point thời gian nghỉ của đội: "+ point);
            }
            matchTime.put(team2.getID(),date);

            // số trận diễn ra cùng lúc
            String checkSameTime = adn.getTime()+"_"+adn.getDate();
            if(matchSameTime.keySet().contains(checkSameTime)){
                matchSameTime.put(checkSameTime,matchSameTime.get(checkSameTime)+1);
                if(matchSameTime.get(checkSameTime) > tournament.getMatchSameTime()){
                    double pointCurrent = solutionInfo.get("point") - 500;
                    solutionInfo.put("point", pointCurrent);
                    double errorCurrent = solutionInfo.get("error") + 1;
                    solutionInfo.put("error", errorCurrent);
                }
            }else{
                matchSameTime.put(checkSameTime,1);
            }
        }
        return solutionInfo;
    }

    private List<Solution> selection(List<Solution> population, int numberOfTournament, int numberOfBest, double bestFitness_){
        Random random = new Random();
        // danh sách các cá thể tốt
        List<Solution> bestSolutions = new ArrayList<>();

        // chạy đến khi tìm được số cá thể vượt trội yêu cầu
        while (bestSolutions.size() < numberOfBest){
            Solution bestSolution = null;
            double bestFitness = bestFitness_;
            // tạo tournament để đánh giá
            for (int i = 0; i < numberOfTournament; i++) {
                // lấy ngẫu nhiên cá thể
                Solution randomSolution = population.get(random.nextInt(population.size()));
                // tính fitness cá thể
                double randomSolutionFitness = randomSolution.getPoint();
                // nếu fitness cá thể trong tournament tốt nhất thì chọn làm cá thể tốt
                if(bestSolution == null || randomSolutionFitness > bestFitness){
                    bestSolution = randomSolution;
                    bestFitness = randomSolutionFitness;
                }
            }
            // thêm cá thể vào danh sách cá thể tốt
            bestSolutions.add(bestSolution);

        }
        return bestSolutions;
    }

    private List<ADN> crossover(List<ADN> parent1,List<ADN> parent2){
        Random random = new Random();
        int randomPointCrossover = random.nextInt(parent1.size());
        List<ADN> subList1 = parent1.subList(0, randomPointCrossover);
        List<ADN> subList2 = parent2.subList(randomPointCrossover, parent2.size());
        List<ADN> child = new ArrayList<>(subList1);
        child.addAll(subList2);
        return child;
    }

    private void mutation(List<ADN> solution){
        Random rand = new Random();
        int index1 = rand.nextInt(solution.size());
        int index2 = rand.nextInt(solution.size());

        while (index1 == index2) {
            index2 = rand.nextInt(solution.size());
        }

        ADN adn1 = solution.get(index1);
        ADN adn2 = solution.get(index2);

        // Trao đổi giữa adn1 và adn2
        LocalDate tempDate = adn1.getDate();
        LocalTime tempTime = adn1.getTime();
        String tempWeek = adn1.getWeek();
        Integer tempWeekday = adn1.getWeekday();

        adn1.setDate(adn2.getDate());
        adn1.setTime(adn2.getTime());
        adn1.setWeek(adn2.getWeek());
        adn1.setWeekday(adn2.getWeekday());

        adn2.setDate(tempDate);
        adn2.setTime(tempTime);
        adn2.setWeek(tempWeek);
        adn2.setWeekday(tempWeekday);
    }

    private void errorSave(List<ADN> solution){
        Map<String,Integer> errorSameTime = new HashMap<>();
        Map<Integer,Match_> errorBreakTime = new HashMap<>();

        String roundTitle = "";

        for (ADN adn : solution){
            Match_ match = matchService.getMatchById(adn.getMatchID());
            roundTitle = match.getType();
            match.setDate(adn.getDate());
            match.setTime(adn.getTime());
            match.setStatus("ingame");
            matchService.createMatch(match);

            Team team1 = match.getTeam1();
            Team team2 = match.getTeam2();

            // lưu thông tin lỗi trùng lịch học ngày thi đấu
            errorMatchSubjectSave(adn,team1,match.getType());
            errorMatchSubjectSave(adn,team2,match.getType());

            // lưu thông tin số trận diễn ra cùng lúc
            String dateTimeMatch = adn.getDate()+"_"+adn.getTime();
            if(errorSameTime.keySet().contains(dateTimeMatch)){
                Integer teamsErrorSameTime = errorSameTime.get(dateTimeMatch) + 1;
                errorSameTime.put(dateTimeMatch,teamsErrorSameTime);
            }else{
                errorSameTime.put(dateTimeMatch,1);
            }

            // lưu thông tin lỗi thời gian nghỉ của đội
            if(errorBreakTime.keySet().contains(team1.getID())){
                Match_ prevMatch = errorBreakTime.get(team1.getID());
                long daysBetween = ChronoUnit.DAYS.between(prevMatch.getDate(), match.getDate());
                if(daysBetween <= getTournamentCurrent().getBreakTimeTeam()){
                    ErrorMatchBreak errorMatchBreak = new ErrorMatchBreak();
                    errorMatchBreak.setTournament(getTournamentCurrent());
                    if(match.getType().contains("group-stage")){
                        errorMatchBreak.setRoundTitle("group-stage");
                    }else{
                        errorMatchBreak.setRoundTitle(match.getType());
                    }
                    errorMatchBreak.setMatch1(prevMatch);
                    errorMatchBreak.setMatch2(match);
                    errorMatchBreak.setTeamID(team1.getID());
                    errorMatchBreak.setDateBetween((int)daysBetween);

                    if(daysBetween == 0){
                        if(match.getTime().isAfter(prevMatch.getTime())){
                            long minutesUntilEndTime = match.getTime().until(prevMatch.getTime(), ChronoUnit.MINUTES);
                            errorMatchBreak.setTimeBetween((int)minutesUntilEndTime);
                        }else if(match.getTime().equals(prevMatch.getTime())){
                            long minutesUntilEndTime = 0;
                            errorMatchBreak.setTimeBetween((int)minutesUntilEndTime);
                        }else {
                            long minutesUntilEndTime = - prevMatch.getTime().until(match.getTime(), ChronoUnit.MINUTES);
                            errorMatchBreak.setTimeBetween((int)minutesUntilEndTime);
                        }
                    }

                    errorMatchBreakService.saveErrorMatchBreak(errorMatchBreak);
                }
            }
            errorBreakTime.put(team1.getID(),match);

            if(errorBreakTime.keySet().contains(team2.getID())){
                Match_ prevMatch = errorBreakTime.get(team2.getID());
                long daysBetween = ChronoUnit.DAYS.between(prevMatch.getDate(), match.getDate());
                if(daysBetween <= getTournamentCurrent().getBreakTimeTeam()){
                    ErrorMatchBreak errorMatchBreak = new ErrorMatchBreak();
                    errorMatchBreak.setTournament(getTournamentCurrent());
                    if(match.getType().contains("group-stage")){
                        errorMatchBreak.setRoundTitle("group-stage");
                    }else{
                        errorMatchBreak.setRoundTitle(match.getType());
                    }
                    errorMatchBreak.setMatch1(prevMatch);
                    errorMatchBreak.setMatch2(match);
                    errorMatchBreak.setTeamID(team2.getID());



                    errorMatchBreak.setDateBetween((int)daysBetween);
                    errorMatchBreakService.saveErrorMatchBreak(errorMatchBreak);
                }

            }
            errorBreakTime.put(team2.getID(),match);
        }

        for (String errorDateTime: errorSameTime.keySet()){
            if(errorSameTime.get(errorDateTime) > getTournamentCurrent().getMatchSameTime()){
                LocalDate errorDate = LocalDate.parse(errorDateTime.split("_")[0]);
                LocalTime errorTime = LocalTime.parse(errorDateTime.split("_")[1]);

                ErrorMatchSameTime errorMatchSameTime = new ErrorMatchSameTime();
                errorMatchSameTime.setTournament(getTournamentCurrent());
                errorMatchSameTime.setRoundTitle(roundTitle);
                errorMatchSameTime.setDate(errorDate);
                errorMatchSameTime.setTime(errorTime);
                errorMatchSameTime.setNumberMatch(errorSameTime.get(errorDateTime));
                errorMatchSameTimeService.saveErrorMatchSameTime(errorMatchSameTime);
            }
        }
    }

    private void errorMatchSubjectSave(ADN adn, Team team,String roundTitle){
        List<Player> listPlayerTeam = playerService.getPlayersByTeamIDAndStatus(team.getID(),"play");
        List<String> studentIDs = listPlayerTeam.stream().map(Player::getStudentID).collect(Collectors.toList());

        Match_ match = matchService.getMatchById(adn.getMatchID());

        int shift = findShift(adn.getTime()).getShiftNumber();
        List<ScheduleSubject> scheduleSubjects = scheduleSubjectService.getScheduleSubjectsByShiftAndWeekdayAndWeek(getSchoolYearCurrent().getID(),shift, adn.getWeekday(), adn.getWeek());

        for(ScheduleSubject scheduleSubject : scheduleSubjects){
            List<ScheduleStudent> scheduleStudents = scheduleStudentService.getScheduleStudentByScheduleSubjectIDAndStudentID(scheduleSubject.getID(),studentIDs);
            if(scheduleStudents.size() > 0){
                List<ErrorMatchSubject> errorMatchSubjects = new ArrayList<>();
                for(ScheduleStudent scheduleStudent : scheduleStudents){
                    ErrorMatchSubject errorMatchSubject = new ErrorMatchSubject();
                    errorMatchSubject.setTournament(getTournamentCurrent());
                    errorMatchSubject.setMatch(match);
                    if(roundTitle.contains("group-stage")){
                        errorMatchSubject.setRoundTitle("group-stage");
                    }else{
                        errorMatchSubject.setRoundTitle(roundTitle);
                    }

                    errorMatchSubject.setScheduleStudent(scheduleStudent);
                    errorMatchSubjects.add(errorMatchSubject);
                }
                errorMatchSubjectService.createAllByList(errorMatchSubjects);
            }
        }
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

    @Async("progressFile")
    public CompletableFuture<Void> GeneticAlgorithm(){

        return CompletableFuture.runAsync(() -> {
            transactionTemplate.execute(status -> {

                System.out.println("start");

                Tournament tournament = getTournamentCurrent();

                // lấy các thông tin vòng hiện tại
                RoundSchedule roundSchedule = roundScheduleService.getRoundScheduleByStatus(tournament.getID(),"inprogress").get(0);
                LocalDate dateStart = roundSchedule.getDateStart();
                LocalDate dateEnd = roundSchedule.getDateEnd();

                errorMatchBreakService.deleteByTournamentIDAndRoundTitle(getTournamentCurrent().getID(),roundSchedule.getRoundTitle());
                errorMatchSameTimeService.deleteByTournamentIDAndRoundTitle(getTournamentCurrent().getID(),roundSchedule.getRoundTitle());
                errorMatchSubjectService.deleteByTournamentIDAndRoundTitle(getTournamentCurrent().getID(),roundSchedule.getRoundTitle());
                // thông tin trận đấu
                // thời gian 1 trận
                int time1Match = tournament.getTimeHalf() * 2 + tournament.getTimeBreak();
                // khung giờ thi đấu bắt đầu buổi sáng
                LocalTime timeStartMorning = roundSchedule.getTimeMorningStart();
                // khung giờ thi đấu kết thúc buổi sáng
                LocalTime timeEndMorning = roundSchedule.getTimeMorningEnd();
                // khung giờ thi đấu kết thúc buổi chiều
                LocalTime timeStartEvening = roundSchedule.getTimeEveningStart();
                // khung giờ thi đấu kết thúc buổi chiều
                LocalTime timeEndEvening = roundSchedule.getTimeEveningEnd();

                // Tạo khung giờ có thể thi đấu trong ngày
                List<LocalTime> timeMatchs = new ArrayList<>();

                LocalTime currentTime = timeStartMorning;
                while (currentTime.plusMinutes(time1Match).isBefore(timeEndMorning.plusMinutes(tournament.getTimeHalf()))||
                        currentTime.plusMinutes(time1Match).equals(timeEndMorning.plusMinutes(tournament.getTimeHalf()))){
                    timeMatchs.add(currentTime);
                    currentTime = currentTime.plusMinutes(time1Match + 15);
                }

                currentTime = timeStartEvening;
                while (currentTime.plusMinutes(time1Match).isBefore(timeEndEvening.plusMinutes(tournament.getTimeHalf()))||
                        currentTime.plusMinutes(time1Match).equals(timeEndEvening.plusMinutes(tournament.getTimeHalf()))){
                    timeMatchs.add(currentTime);
                    currentTime = currentTime.plusMinutes(time1Match + 15);
                }

                // lấy các trận đấu của vòng hiện tại
                List<Match_> matchList = null;
                if(roundSchedule.getRoundTitle().contains("group-stage")){
                    matchList = matchService.getMatchByTypeForGA(tournament.getID(),"group-stage");
                }else if(!roundSchedule.getRoundTitle().equals("final")) {
                    matchList = matchService.getMatchByType(tournament.getID(),roundSchedule.getRoundTitle());
                }else{
                    System.out.println(123);
                    matchList = matchService.getMatchByType(tournament.getID(),"third-place");
                    matchList.addAll(matchService.getMatchByType(tournament.getID(),"final"));
                }

                // Tạo danh sách ngày có thể thi đấu
                // tất cả trận đấu
                Map<String,Double> dateAllMatchs = new HashMap<>();
                // danh sách ngày thi đấu
                List<LocalDate> dateMatchs = new ArrayList<>();

                System.out.println("create date time");

                // duyệt từng ngày từ ngày bắt đầu vòng
                for (LocalDate date = dateStart; !date.isAfter(dateEnd); date = date.plusDays(1)) {
                    // thêm ngày thi đấu vào danh sách
                    SemesterWeek semesterWeek = semesterWeekService.getSemesterWeekByDate(getSchoolYearCurrent().getID(),date);
                    String numberOfWeek = semesterWeek.getNumWeek();
                    PlanYear planYear = planYearService.getPlanYearByWeek(getSchoolYearCurrent().getID(), numberOfWeek);

                    if(planYear != null){
                        LocalDate planYearStart = planYear.getDate();
                        LocalDate planYearEnd = planYearStart.plusDays(planYear.getNumDate());
                        // kiểm tra tránh các ngày nghỉ lễ
                        if((!date.equals(planYearStart)) || (!date.isAfter(planYearStart) && !date.isBefore(planYearEnd)) || (!date.equals(planYearEnd))){
                            dateMatchs.add(date);
                            // thêm ngày và giờ thi đấu và đánh dấu 0 lần gặp
                            for(LocalTime time : timeMatchs){
                                String dateTime = date.toString()+"_"+time.toString();
                                dateAllMatchs.put(dateTime,0.0);
                            }
                        }
                    }else{
                        dateMatchs.add(date);
                        for(LocalTime time : timeMatchs){
                            String dateTime = date.toString()+"_"+time.toString();
                            dateAllMatchs.put(dateTime,0.0);
                        }
                    }
                }

                int toltalMatchPopulation = 0;

                System.out.println("create population");

                Random random = new Random();
                List<Solution> population = new ArrayList<>();

                int populationSize = matchList.size() > 10 ? 50 : matchList.size()*2;
                // Tạo quần thể
                for (int i = 0; i < populationSize; i++) {
//                    System.out.println("quần thể"+i);
                    // tạo 1 cá thể
                    Solution solution = new Solution();
                    // tạo 1 tập hợp adn
                    List<ADN> individual = new ArrayList<>();

                    Map<String,Integer> numberSelectMatch = new HashMap<>();

                    // duyệt từng trận trong danh sách trận của vòng
                    for (Match_ match_ : matchList){
                        LocalDate randomDate;
                        LocalTime randomTime;
                        while(true){
                            // chọn ngày ngẫu nhiên
                            int ramdomIndexDate = random.nextInt(dateMatchs.size());
                            randomDate = dateMatchs.get(ramdomIndexDate);
                            // chọn giờ ngẫu nhiên
                            int randomIndexTime = random.nextInt(timeMatchs.size());
                            randomTime = timeMatchs.get(randomIndexTime);

                            String dateTimeRandom = randomDate.toString()+"_"+randomTime.toString();
                            // thực hiện yêu cầu số trận diễn ra cùng lúc
                            // lấy ngày và giờ kiểm tra trong đánh dấu
                            // ngày giờ trận đấu chưa lớn hơn yêu cầu thì nhận
                            if(numberSelectMatch.keySet().contains(dateTimeRandom)){
                                if(numberSelectMatch.get(dateTimeRandom) < tournament.getMatchSameTime() && (dateAllMatchs.get(dateTimeRandom) / toltalMatchPopulation < 0.7)){
                                    numberSelectMatch.put(dateTimeRandom,numberSelectMatch.get(dateTimeRandom) +1);
                                    toltalMatchPopulation += 1;
                                    dateAllMatchs.put(dateTimeRandom,dateAllMatchs.get(dateTimeRandom) + 1);
                                    break;
                                }
                            }else {
                                numberSelectMatch.put(dateTimeRandom,1);
                                toltalMatchPopulation += 1;
                                dateAllMatchs.put(dateTimeRandom,1.0);
                                break;
                            }
                        }

                        // lấy thứ trong tuần từ ngày ngẫy nhiên 1-7(cn-t7)
                        DayOfWeek dayOfWeek = randomDate.getDayOfWeek();
                        int weekdayRaw = dayOfWeek.getValue();
                        int weekdayTrans = weekdayRaw == 7 ? 1 : weekdayRaw + 1;

                        // lấy thông tin tuần học của học kỳ từ ngày
                        SemesterWeek semesterWeek = semesterWeekService.getSemesterWeekByDate(getSchoolYearCurrent().getID(),randomDate);
                        ADN adn = new ADN();
                        adn.setMatchID(match_.getID());
                        adn.setWeek(semesterWeek.getNumWeek());
                        adn.setWeekday(weekdayTrans);
                        adn.setTime(randomTime);
                        adn.setDate(randomDate);
                        // thêm adn vào tập hợp adn
                        individual.add(adn);
                    }
                    // thêm tập hợp adn vào cá thể
                    solution.setAdns(new ArrayList<>(individual));
                    // thêm cá thể vào quần thể
                    population.add(solution);
                }

                // chọn thế hệ để quần thể tiến hóa
                int countGenerations = 0;
                // số thế hệ
                int numberOfGenerations  = 50;
                int indexBestSolution = -1;
                int indexBestSolutionNoError = -1;

                boolean checkError = false;

                while(countGenerations < numberOfGenerations){
                    double sumFitness = 0.0;

                    double minError = Double.MAX_VALUE;
                    double maxFitnessNoError = 0.0;


                    //  fitness
                    for (int i = 0; i < population.size(); i++) {
                        Solution solution = population.get(i);
                        // cập nhật fitnes cho mỗi cá thể
                        Map<String,Double> solutionInfo = fitness(solution.getAdns());
                        solution.setPoint(solutionInfo.get("point"));
                        solution.setError(solutionInfo.get("error"));
                        if(solutionInfo.get("error") == 0.0){
                            checkError = true;
                            if(solutionInfo.get("point") > maxFitnessNoError){
                                maxFitnessNoError = solutionInfo.get("point");
                                indexBestSolutionNoError = i;
                            }
                        }
                        if(!checkError){
                            if(solutionInfo.get("error") < minError){
                                minError = solutionInfo.get("error");
                                indexBestSolution = i;
                            }
                        }
                        sumFitness += solution.getPoint();
        //            System.out.println("fitness: "+solution.getPoint());
                    }

                    if(checkError){
                        break;
                    }

                    // tính trung bình fitness
                    double averageFitness = sumFitness / population.size();

                    // chọn lọc cá thể
                    population = selection(population,3, population.size()/2,averageFitness);
                    int newPopulationSize = population.size();

                    // lai tạo
                    for (int i = 0; i < populationSize - newPopulationSize; i++) {
                        int min = 0;
                        int max = population.size() - 1;
                        int count = 2;

                        List<Integer> randomIndexs = new ArrayList<>();

                        while (randomIndexs.size() < count){
                            int randomIndex = random.nextInt(max - min + 1) + min;
                            if(!randomIndexs.contains(randomIndex)){
                                randomIndexs.add(randomIndex);
                            }
                        }

        //            System.out.println("radom parent: "+randomIndexs.toString());

                        List<ADN> parent1 = population.get(randomIndexs.get(0)).getAdns();
                        List<ADN> parent2 = population.get(randomIndexs.get(1)).getAdns();

                        List<ADN> child = crossover(parent1,parent2);
                        Solution solution = new Solution(child);
                        population.add(solution);
                    }

                    // đột biến
                    for(Solution solution: population){
                        double mutationRate = 0.02;
                        if (random.nextDouble() < mutationRate) {
                            mutation(solution.getAdns());
                        }
                    }
                    countGenerations += 1;
                }

                // ghi file log để kiểm tra cá thể
                String dir = System.getProperty("user.dir")+"/"+fileLogs;

                System.out.println("get solution");

                try {
                    FileWriter writer = new FileWriter(dir+"/logs.txt");
                    if(indexBestSolutionNoError >= 0 ){
                        Solution bestSolution = population.get(indexBestSolutionNoError);
                        // cập nhật thời gian cho trận đấu
                        for (ADN adn : bestSolution.getAdns()){
                            Match_ match = matchService.getMatchById(adn.getMatchID());
                            match.setDate(adn.getDate());
                            match.setTime(adn.getTime());
                            match.setStatus("ingame");
                            matchService.createMatch(match);
                        }

                        roundSchedule.setError((int)bestSolution.getError());
                        roundScheduleService.createRoundSchedule(roundSchedule);

                        // ghi log
                        writer.write("solution best:"+indexBestSolutionNoError+"\n");
                        for (ADN adn : bestSolution.getAdns()){
                            writer.write( adn.toString()+ "\n");
                        }
                        writer.write("fitness:"+bestSolution.getPoint()+"\n");
                        writer.write("error:"+bestSolution.getError()+"\n");
                        writer.write("-------------------------------------\n");
                    } else if (indexBestSolution >= 0) {
                        Solution solution = population.get(indexBestSolution);
                        // cập nhật thời gian cho trận đấu
                        // ghi lỗi

                        roundSchedule.setError((int)solution.getError());
                        roundScheduleService.createRoundSchedule(roundSchedule);

                        errorSave(solution.getAdns());

                        writer.write("solution:"+indexBestSolution+"\n");
                        for (ADN adn : solution.getAdns()){
                            writer.write( adn.toString()+ "\n");
                        }
                        writer.write("fitness:"+solution.getPoint()+"\n");
                        writer.write("error:"+solution.getError()+"\n");
                        writer.write("-------------------------------------\n");
                    }

                    writer.close();
                } catch (IOException e) {
                    System.out.println("An error occurred while writing to the file.");
                    e.printStackTrace();
                }

                tournament.setStatus("inprogress");
                tournamentService.createTournament(tournament);

                System.out.println("end");

                return null;
            });
        });
    }
}
