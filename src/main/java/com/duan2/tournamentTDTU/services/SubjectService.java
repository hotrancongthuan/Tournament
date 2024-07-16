package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.models.Subject;
import com.duan2.tournamentTDTU.repositorys.AccountRepository;
import com.duan2.tournamentTDTU.repositorys.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SubjectService {

    @Autowired
    private SubjectRepository subjectRepository;

    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    public Subject getSubjectById(String subjectId) {
        return subjectRepository.findById(subjectId).orElse(null);
    }

    public Subject createSubject(Subject subject) {
        // Các logic kiểm tra và xử lý trước khi lưu vào cơ sở dữ liệu
        return subjectRepository.save(subject);
    }



    // Các phương thức khác tương tự...
}