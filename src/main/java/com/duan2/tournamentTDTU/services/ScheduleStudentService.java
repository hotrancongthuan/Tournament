package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.ScheduleStudent;
import com.duan2.tournamentTDTU.models.ScheduleSubject;
import com.duan2.tournamentTDTU.models.Student;
import com.duan2.tournamentTDTU.repositorys.ScheduleStudentRepository;
import com.duan2.tournamentTDTU.repositorys.ScheduleSubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleStudentService {

    @Autowired
    private ScheduleStudentRepository scheduleStudentRepository;

    public List<ScheduleStudent> getAllScheduleStudents() {
        return scheduleStudentRepository.findAll();
    }

    public ScheduleStudent getScheduleStudentById(Integer scheduleStudentId) {
        return scheduleStudentRepository.findById(scheduleStudentId).orElse(null);
    }

    public ScheduleStudent getScheduleStudentBySubjectAndStudent(String scheduleSubjectId, String studentID) {
        return scheduleStudentRepository.findByScheduleSubjectIDAndStudentID(scheduleSubjectId,studentID).orElse(null);
    }

    public List<ScheduleStudent> getScheduleStudentBySubject(String scheduleSubjectId) {
        return scheduleStudentRepository.findByScheduleSubjectID(scheduleSubjectId).orElse(null);
    }

    public List<ScheduleStudent> getScheduleStudentByScheduleSubjectIDAndStudentID(String scheduleSubjectId, List<String> studentIDs) {
        return scheduleStudentRepository.findByScheduleSubjectIDAndListStudentID(scheduleSubjectId,studentIDs).orElse(null);
    }

    public List<ScheduleStudent> getScheduleStudentByStudent(String studentID) {
        return scheduleStudentRepository.findByStudentID(studentID).orElse(null);
    }

    public ScheduleStudent createScheduleStudent(ScheduleStudent scheduleStudent) {
        return scheduleStudentRepository.save(scheduleStudent);
    }


}
