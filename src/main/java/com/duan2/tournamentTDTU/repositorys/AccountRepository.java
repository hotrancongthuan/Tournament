package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, String> {

}
