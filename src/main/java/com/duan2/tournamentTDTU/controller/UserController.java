package com.duan2.tournamentTDTU.controller;

import com.duan2.tournamentTDTU.models.*;
import com.duan2.tournamentTDTU.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;

import java.security.Principal;
import java.util.List;
import java.util.regex.Pattern;

@ControllerAdvice
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private TeamService teamService;

    @Autowired
    private SchoolYearService schoolYearService;

    @Autowired
    private TournamentService tournamentService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private RoundScheduleService roundScheduleService;

    @Autowired
    private MatchService matchService;

    @ModelAttribute("user_current")
    public User addUserToModel(Authentication authentication) {
        if (authentication != null) {
            return userService.getUserById(authentication.getName());
        }
        return null;
    }

    @ModelAttribute("user_role")
    public String addRoleToModel(Authentication authentication) {
        if (authentication != null) {
            return accountService.getAccountById(authentication.getName()).getRole();
        }
        return null;
    }

    @ModelAttribute("tournament_current")
    public Tournament addTournamentToModel(){
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

    @ModelAttribute("schoolYears_")
    public List<SchoolYear> addSchoolYearToModel(){
        List<SchoolYear> schoolYear = schoolYearService.getAllSchoolYears();
        if (schoolYear != null){
            return schoolYear;
        }
        return null;
    }

    @ModelAttribute("studentCurrent")
    public Student addStudentToModel(Authentication authentication){
        if(authentication != null){
            return studentService.getStudentById(authentication.getName());
        }
        return null;
    }

    @ModelAttribute("team_user")
    public Team addTeamToModel(@ModelAttribute("studentCurrent") Student student,
                               @ModelAttribute("tournament_current") Tournament tournament){
        if(student != null && tournament != null){
            return teamService.getTeamByClass1OrClass2(tournament.getID(),student.getClassID(),student.getClassID());
        }
        return null;
    }

    @ModelAttribute("player_user")
    public Player addPlayerToModel(Authentication authentication,@ModelAttribute("tournament_current") Tournament tournament){
        if(authentication != null && tournament != null){
            return playerService.getPlayerByStudentIDAndTournamentID(authentication.getName(),tournament.getID());
        }
        return null;
    }

    @ModelAttribute("roundSchedule_inprogress")
    public RoundSchedule addRoundScheduleToModel(@ModelAttribute("tournament_current") Tournament tournament){
        if(tournament == null){
            return null;
        }

        List<RoundSchedule> roundSchedule = roundScheduleService.getRoundScheduleByStatus(tournament.getID(),"inprogress");
        if(!roundSchedule.isEmpty()){
            return roundSchedule.get(0);
        }
        return null;
    }

    @ModelAttribute("roundSchedule_inprogress_title")
    public String getTitleRoundSchedule(@ModelAttribute("roundSchedule_inprogress") RoundSchedule roundSchedule){
        if(roundSchedule == null){
            return null;
        }
        String title = roundSchedule.getRoundTitle();
        if(title != null){
            if(title.contains("playoff")){
                return "Vòng playoff";
            } else if(title.contains("group-stage")){
                return "Vòng bảng";
            } else if(title.contains("round-of-16")){
                return "Vòng 1/16";
            } else if(title.contains("quarterfinals")){
                return "Tứ kết";
            }else if(title.contains("semi-final")){
                return "Bán kết";
            }else if(title.contains("final")){
                return "Chung kết";
            }
        }
        return null;
    }

    @ModelAttribute("checkMatchAttendance")
    public Integer checkMatchAttendance(
            Authentication authentication,
            @ModelAttribute("user_role") String role,
            @ModelAttribute("tournament_current") Tournament tournament,
            @ModelAttribute("player_user") Player player){
        if(player != null && tournament != null && role != null){
            if(role.equals("ROLE_PLAYER") && player.getCaptain() == 1){
                List<Match_> matchPlaying = matchService.getMatchToAttendance(tournament.getID(),player.getTeam().getID(), "playing");
                if(matchPlaying.size() == 0){
                    List<Match_> matchPrepare = matchService.getMatchToAttendance(tournament.getID(),player.getTeam().getID(), "ingame");
                    if(matchPrepare.size() > 0){
                        return matchPrepare.get(0).getID();
                    }
                }
            }
        }
        return -1;
    }

    @ModelAttribute("studentData")
    public boolean checkStudent(){
        List<Student> student = studentService.getAllStudents();
        if(!student.isEmpty()){
            return true;
        }
        return false;
    }
}
