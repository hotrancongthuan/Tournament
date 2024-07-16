package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.ScheduleStudent;
import com.duan2.tournamentTDTU.models.ScheduleSubject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleStudentRepository extends JpaRepository<ScheduleStudent, Integer> {
    Optional<ScheduleStudent> findByScheduleSubjectIDAndStudentID(String scheduleSubjectID, String studentID);

    Optional<List<ScheduleStudent>> findByScheduleSubjectID(String scheduleSubjectID);

    Optional<List<ScheduleStudent>> findByStudentID(String studentID);

    @Query("SELECT ss FROM ScheduleStudent ss WHERE ss.scheduleSubject.ID = :scheduleSubjectId AND ss.student.ID IN :studentIDs")
    Optional<List<ScheduleStudent>> findByScheduleSubjectIDAndListStudentID(@Param("scheduleSubjectId")String scheduleSubjectId, @Param("studentIDs") List<String> studentIDs);
}
