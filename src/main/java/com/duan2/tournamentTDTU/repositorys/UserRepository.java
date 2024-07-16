package com.duan2.tournamentTDTU.repositorys;

import com.duan2.tournamentTDTU.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
