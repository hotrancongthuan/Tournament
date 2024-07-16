package com.duan2.tournamentTDTU.fileUpload;

import com.duan2.tournamentTDTU.models.*;
import com.duan2.tournamentTDTU.services.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class ProcessFileService {

    @Value("${file.path}")
    private String filePath;

    @Autowired
    private SchoolYearService schoolYearService;

    @Autowired
    private TakeInService takeInService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private UserService userService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private PlanYearService planYearService;

    @Autowired
    private SemesterWeekService semesterWeekService;

    @Autowired
    private ScheduleSubjectService scheduleSubjectService;

    @Autowired
    private ScheduleStudentService scheduleStudentService;

    @Autowired
    private UploadProcessService uploadProcessService;

    @Async("progressFile")
    public CompletableFuture<Void> processFileStudent() {
        String dir = System.getProperty("user.dir")+"/"+filePath;

        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");

        String filename = "student.csv";

        try (BufferedReader reader = new BufferedReader(new FileReader(dir + "/" + filename))) {
            String headerString = reader.readLine();
            String[] header = headerString.split(",");

            List<Student> students = new ArrayList<>();

            List<Account> accounts = new ArrayList<>();

            List<User> users = new ArrayList<>();

            Set<String> takeInSet = new HashSet<>();

            String line;
            while((line = reader.readLine()) != null){
                String[] std = line.split(",");

                //takeIn
                takeInSet.add(std[3]);
                //student
                Student student = new Student(std[0],new TakeIn(std[3]),std[4],std[5]);
                students.add(student);
                //user
                User user = new User(std[0],std[1],std[2]);
                users.add(user);
                //account
                Account account = new Account(std[0],"password",0,"ROLE_STUDENT");
                accounts.add(account);

            }

            //create take in
            for(String takeIn: takeInSet){
                String takeInSub = takeIn.substring(1);
                int takeInTrans = Integer.parseInt(takeInSub);
                int takeInRange = takeInTrans - 21;
                Integer year = 2017 + takeInRange;
                TakeIn takeIn1 = new TakeIn(takeIn,year);
                takeInService.createTakeIn(takeIn1);
            }

            //create student
            for(Student student: students){
                TakeIn takeIn = takeInService.getTakeInById(student.getTakeIn().getID());
                student.setTakeIn(takeIn);
                studentService.createStudent(student);
            }

            //create user
            for(User user: users){
                userService.createUser(user);
            }

            //create account
            for(Account account: accounts){
                accountService.createAccount(account);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {

            uploadProcessService.setThreadFinish("addStudent");
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async("progressFile")
    public CompletableFuture<Void> processFilePlanYear(){

        String dir = System.getProperty("user.dir")+"/"+filePath;
        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
        String filename = "planYear.csv";

        List<PlanYear> planYears = planYearService.getPlanYearBySchoolYear(schoolYear.getID());

        if(!planYears.isEmpty()){
            planYearService.deleteAllPlanYear(planYears);
        }

        if(schoolYear != null){
            try (BufferedReader reader = new BufferedReader(new FileReader(dir + "/" + filename))) {
                String headerString = reader.readLine();
                String[] header = headerString.split(",");
                String line;
                while((line = reader.readLine()) != null) {
                    String[] plan = line.split(",");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate date = LocalDate.parse(plan[2], formatter);
                    PlanYear planYear = new PlanYear(plan[0],plan[1],date,Integer.parseInt(plan[3]),schoolYear);
                    planYearService.createPlanYear(planYear);
                }

            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                uploadProcessService.setThreadFinish("addPlanYear");
            }
        }

        return CompletableFuture.completedFuture(null);
    }

    @Async("progressFile")
    public CompletableFuture<Void> processFileWeek(){

        String dir = System.getProperty("user.dir")+"/"+filePath;
        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
        String filename = "week.csv";

        List<SemesterWeek> semesterWeeks = semesterWeekService.getSemesterWeekBySchoolYear(schoolYear.getID());

        if(!semesterWeeks.isEmpty()){
            semesterWeekService.deleteSemesterWeek(semesterWeeks);
        }

        if(schoolYear != null){
            try (BufferedReader reader = new BufferedReader(new FileReader(dir + "/" + filename))) {
                String headerString = reader.readLine();
                String[] header = headerString.split(",");
                String line;
                while((line = reader.readLine()) != null) {
                    String[] week = line.split(",");
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate dateStart = LocalDate.parse(week[2], formatter);
                    LocalDate dateEnd = LocalDate.parse(week[3], formatter);
                    SemesterWeek semesterWeek = new SemesterWeek(week[0],week[1],dateStart,dateEnd,schoolYear);
                    semesterWeekService.createSemesterWeek(semesterWeek);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                uploadProcessService.setThreadFinish("addWeek");
            }
        }
        return CompletableFuture.completedFuture(null);
    }

    @Async("progressFile")
    public CompletableFuture<Void> processFileScheduleSubject(){

        String dir = System.getProperty("user.dir")+"/"+filePath;
        SchoolYear schoolYear = schoolYearService.getSchoolYearByCurrent("1");
        String filename = "scheduleSubject.csv";
        if(schoolYear != null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(dir + "/" + filename))) {
                String headerString = reader.readLine();
                String[] header = headerString.split(",");

                String line;
                while ((line = reader.readLine()) != null) {
                    String[] schedule = line.split(",");
                    ScheduleSubject scheduleSubject = new ScheduleSubject(schedule[0], schedule[1].substring(1), schedule[2], Integer.parseInt(schedule[3]), Integer.parseInt(schedule[4]), schedule[5], schedule[6]);
                    scheduleSubject.setSchoolYear(schoolYear);
                    ScheduleSubject new_scheduleSubject = scheduleSubjectService.createScheduleSubject(scheduleSubject);
                    String[] students = schedule[7].substring(1, schedule[7].length() - 1).split("--");
                    for (String studentID : students) {
                        Student student = studentService.getStudentById(studentID);
                        ScheduleStudent scheduleStudent = new ScheduleStudent(new_scheduleSubject, student);
                        scheduleStudentService.createScheduleStudent(scheduleStudent);
                    }
                }

            } catch (IOException e) {
                System.out.println("LOI ROI");
            }finally {
                uploadProcessService.setThreadFinish("addScheduleSubject");
            }
        }
        return CompletableFuture.completedFuture(null);
    }

}
