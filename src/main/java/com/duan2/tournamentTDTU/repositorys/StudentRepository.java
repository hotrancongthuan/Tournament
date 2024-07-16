package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Student;
import com.duan2.tournamentTDTU.models.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface StudentRepository extends JpaRepository<Student, String> {
    @Query("SELECT DISTINCT s.classID FROM Student s")
    Set<String> findDistinctClassID();

    @Query("SELECT DISTINCT s.takeIn.id FROM Student s")
    Set<String> findDistinctTakeIn();

    @Query("SELECT DISTINCT s.classID FROM Student s WHERE s.takeIn.id = :takeInID")
    Set<String> findDistinctClassIDByTakeIn(@Param("takeInID") String takeInID);

    @Query("SELECT s FROM Student s WHERE " +
            "(:studentID IS NULL OR s.ID = :studentID) AND " +
            "(:takeInID IS NULL OR s.takeIn.id = :takeInID) AND " +
            "(:class IS NULL OR s.classID = :class)")
    Page<Student> findByIDAndTakeInIDAndClass(@Param("studentID") String studentID,@Param("takeInID")  String takeInID,@Param("class") String class_, Pageable pageable);

    Optional<List<Student>> findByClassID(String classID);
}
