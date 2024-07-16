package com.duan2.tournamentTDTU.updateMatch;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class CardData {
    private Integer playerCard;
    private String typeCard;
    private Integer minutesCard;
}
