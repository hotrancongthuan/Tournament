package com.duan2.tournamentTDTU.updateMatch;

import com.duan2.tournamentTDTU.services.MatchService;
import com.duan2.tournamentTDTU.services.PlayerService;
import com.duan2.tournamentTDTU.services.TeamService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UpdateMatch {

    @Autowired
    private MatchService matchService;

    @Autowired
    private PlayerService playerService;

    @Autowired
    private TeamService teamService;


}
