package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.ScheduleSubject;
import com.duan2.tournamentTDTU.models.Student;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ScheduleSubjectRepository  extends JpaRepository<ScheduleSubject, String> {
    @Query("SELECT s FROM ScheduleSubject s WHERE " +
            "(:nameSubject IS NULL OR s.name LIKE CONCAT('%',:nameSubject,'%')) AND " +
            "(:shift IS NULL OR s.shift = :shift) AND " +
            "(:semester IS NULL OR s.semester IN :semester) AND " +
            "(:school_yearID IS NULL OR s.schoolYear.id = :school_yearID) AND " +
            "(:weekday IS NULL OR s.weekday = :weekday)")
    Page<ScheduleSubject> findByNameAndShiftAndWeekday(@Param("nameSubject") String nameSubject, @Param("shift")  String shift, @Param("weekday") String weekday, @Param("semester") List<String> semester, @Param("school_yearID") Integer school_yearID, Pageable pageable);


    Optional<List<ScheduleSubject>> findBySchoolYearID(Integer schoolYearID);

    @Query("SELECT s FROM ScheduleSubject s WHERE " +
            "(:week IS NULL OR s.week LIKE CONCAT('%',:week,'%')) AND " +
            "(:shift IS NULL OR s.shift = :shift) AND " +
            "(:schoolYearID IS NULL OR s.schoolYear.ID = :schoolYearID) AND " +
            "(:weekday IS NULL OR s.weekday = :weekday)")
    Optional<List<ScheduleSubject>> findByShiftAndWeekdayAndWeek(@Param("schoolYearID")Integer schoolYearID,@Param("shift") Integer shift,@Param("weekday") Integer weekday,@Param("week") String week);
}
