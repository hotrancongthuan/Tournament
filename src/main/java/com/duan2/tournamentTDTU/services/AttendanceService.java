package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.models.Attendance;
import com.duan2.tournamentTDTU.models.Student;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import com.duan2.tournamentTDTU.repositorys.AttendanceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    public List<Attendance> getAllAttendances() {
        return attendanceRepository.findAll();
    }

    public Attendance getAttendanceById(Integer attendanceId) {
        return attendanceRepository.findById(attendanceId).orElse(null);
    }

    public List<Attendance> getAttendanceByMatchIDAndTeamID(Integer matchID, Integer teamID) {
        return attendanceRepository.findByMatchIDAndTeamID(matchID,teamID).orElse(null);
    }

    public List<Attendance> getAttendanceByMatchIDAndTeamIDAndStatus(Integer matchID, Integer teamID,String status) {
        return attendanceRepository.findByMatchIDAndTeamIDAndStatus(matchID,teamID,status).orElse(null);
    }

    public Attendance createAttendance(Attendance attendance) {
        return attendanceRepository.save(attendance);
    }

    public List<Attendance> createAllAttendance(List<Attendance> attendances) {
        return attendanceRepository.saveAll(attendances);
    }

    @Transactional
    public void deleteByMatchIDAndTeamID(Integer matchID, Integer teamID){
        attendanceRepository.deleteByMatchIDAndTeamID(matchID,teamID);
    }

}