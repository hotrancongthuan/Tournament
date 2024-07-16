package com.duan2.tournamentTDTU.draw;

//import com.duan2.tournamentTDTU.models.Team;

import java.util.*;

public class DrawMatch {

    public LinkedHashMap<String,Integer> DrawGroupStage(int totalTeam) {

//        List<Integer> listRound = new ArrayList<>();
        LinkedHashMap <String,Integer> map = new LinkedHashMap <>();
        if(totalTeam >= 32){
            // số trận play off
            int round = 1;
            while (true){
                int teamsTarget = 32 * round;
                int matchs = totalTeam - teamsTarget;
                int luckTeams = teamsTarget - matchs;
                if(luckTeams > 0 && matchs > 0){
                    map.put("playoff-"+round,matchs);
                    break;
                }else if(luckTeams > 0 && matchs == 0){
                    break;
                } else if (luckTeams <= 0) {
                    map.put("playoff-"+round,teamsTarget);
                }
                round = round + 1;
            }
        }else if(totalTeam >= 28 && totalTeam < 32){
            int matchs = totalTeam - 28;
            if(matchs > 0){
                map.put("playoff",matchs);
            }
        } else if (totalTeam >= 24 && totalTeam < 28) {
            int matchs = totalTeam - 24;
            if(matchs > 0){
                map.put("playoff",matchs);
            }
        }else if (totalTeam > 16){
            int matchs = totalTeam - 16;
            map.put("playoff",matchs);
        }

        return map;

    }

    public LinkedHashMap<String,Integer> DrawFinal(){
        LinkedHashMap <String,Integer> map = new LinkedHashMap <>();
        //chung kết
        map.put("final",2);
        map.put("semi-final",2);
        map.put("quarterfinals",4);
        map.put("round-of-16",8);

        return map;
    }

    public static void main(String[] args){
        DrawMatch drawMatch = new DrawMatch();


        LinkedHashMap<String,Integer> test = drawMatch.DrawGroupStage(17);
        System.out.println(test);
        System.out.println(test.keySet());

//        drawMatch.DrawFinal();
    }

}
