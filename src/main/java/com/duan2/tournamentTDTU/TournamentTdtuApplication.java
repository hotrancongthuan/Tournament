package com.duan2.tournamentTDTU;

import com.duan2.tournamentTDTU.genetic_algorithm.GeneticAlgorithmService;
import com.duan2.tournamentTDTU.services.AccountService;
import com.duan2.tournamentTDTU.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TournamentTdtuApplication {

	@Autowired
	private TeamService teamService;

	@Autowired
	private AccountService accountService;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@Autowired
	private GeneticAlgorithmService geneticAlgorithmService;

	public static void main(String[] args) {
		SpringApplication.run(TournamentTdtuApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner(String[] args){

		return runner -> {
//			geneticAlgorithmService.GeneticAlgorithm();
		};
	}
}
