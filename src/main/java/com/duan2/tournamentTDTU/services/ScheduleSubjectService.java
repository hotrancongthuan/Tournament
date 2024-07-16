package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.models.ScheduleSubject;
import com.duan2.tournamentTDTU.models.Student;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import com.duan2.tournamentTDTU.repositorys.ScheduleSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleSubjectService {

    @Autowired
    private ScheduleSubjectRepository scheduleSubjectRepository;

    public List<ScheduleSubject> getAllScheduleSubjects(Integer schoolYearID) {
        return scheduleSubjectRepository.findBySchoolYearID(schoolYearID).orElse(null);
    }

    public List<ScheduleSubject> getScheduleSubjectsByNameAndShiftAndWeekInSearch(Integer page, Integer size, String nameSubject, String shift, String weekday,List<String> semester, Integer school_yearID){
        Pageable pageable = PageRequest.of(page, size);
        Page<ScheduleSubject> scheduleSubjects = scheduleSubjectRepository.findByNameAndShiftAndWeekday(nameSubject,shift,weekday,semester,school_yearID,pageable);
        return scheduleSubjects.getContent();
    }

    public  List<ScheduleSubject> getScheduleSubjectsByShiftAndWeekdayAndWeek(Integer schoolYearID, Integer shift, Integer weekday, String week){
        List<ScheduleSubject> scheduleSubjects = scheduleSubjectRepository.findByShiftAndWeekdayAndWeek(schoolYearID,shift,weekday,week).orElse(null);
        return scheduleSubjects;
    }

    public ScheduleSubject getScheduleSubjectById(String scheduleSubjectId) {
        return scheduleSubjectRepository.findById(scheduleSubjectId).orElse(null);
    }

    public ScheduleSubject createScheduleSubject(ScheduleSubject scheduleSubject) {
        ScheduleSubject scheduleSubject1 = getScheduleSubjectById(scheduleSubject.getID());
        if(scheduleSubject1 != null){
            return scheduleSubject1;
        }
        return scheduleSubjectRepository.save(scheduleSubject);
    }

}