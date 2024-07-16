package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Account;
import com.duan2.tournamentTDTU.models.TakeIn;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TakeInRepository extends JpaRepository<TakeIn, String> {
}
