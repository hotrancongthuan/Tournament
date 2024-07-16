package com.duan2.tournamentTDTU.services;

import com.duan2.tournamentTDTU.models.Student;
import com.duan2.tournamentTDTU.models.Subject;
import com.duan2.tournamentTDTU.models.TakeIn;
import com.duan2.tournamentTDTU.repositorys.SubjectRepository;
import com.duan2.tournamentTDTU.repositorys.TakeInRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TakeInService {
    @Autowired
    private TakeInRepository takeInRepository;

    public List<TakeIn> getAllTakeIn() {
        return takeInRepository.findAll();
    }

    public TakeIn getTakeInById(String takeInID) {
        return takeInRepository.findById(takeInID).orElse(null);
    }

    public TakeIn createTakeIn(TakeIn takeIn) {
        TakeIn takeIn1 = getTakeInById(takeIn.getID());
        if(takeIn1 != null){
            return null;
        }
        return takeInRepository.save(takeIn);
    }
}
