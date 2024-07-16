package com.duan2.tournamentTDTU.genetic_algorithm;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class Test {

    @Async("progressThread")
    public CompletableFuture<Void> testThread(){
        for (int i = 0; i < 100; i++){

            try {
                System.out.println(i);
                // Dừng luồng trong 1 giây
                Thread.sleep(1000); // Thời gian được chỉ định bằng mili giây
            } catch (InterruptedException e) {
                // Xử lý ngoại lệ nếu luồng bị gián đoạn trong khi đang sleep
                e.printStackTrace();
            }
        }
        return CompletableFuture.completedFuture(null);
    }
}
