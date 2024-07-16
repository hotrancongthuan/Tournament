package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Student;
import com.duan2.tournamentTDTU.repositorys.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class StudentService {

    @Autowired
    private StudentRepository studentRepository;

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public List<Student> getStudentsByIDAndTakeInIDAndClass(Integer page, Integer size, String studentID, String takeInID, String class_) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Student> studentPage = studentRepository.findByIDAndTakeInIDAndClass(studentID,takeInID,class_,pageable);
        return studentPage.getContent();
    }

    public Student getStudentById(String studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }

    public List<Student> getStudentByClass(String classID) {
        return studentRepository.findByClassID(classID).orElse(null);
    }

    public Student createStudent(Student student) {
        Student student1 = getStudentById(student.getID());
        if(student1 != null){
            return student1;
        }
        return studentRepository.save(student);
    }

    public Set<String> getDistinctTakeIn(){
        return studentRepository.findDistinctTakeIn();
    }

    public Set<String> getDistinctClass(){
        return studentRepository.findDistinctClassID();
    }

    public Set<String> getDistinctClassByTakeIn(String takeInID){
        return studentRepository.findDistinctClassIDByTakeIn(takeInID);
    }

}
