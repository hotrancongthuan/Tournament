package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Integer> {
    void deleteByMatchIDAndTeamID(Integer matchID, Integer teamID);

    Optional<List<Attendance>> findByMatchIDAndTeamID(Integer matchID, Integer teamID);

    Optional<List<Attendance>> findByMatchIDAndTeamIDAndStatus(Integer matchID, Integer teamID,String status);
}
