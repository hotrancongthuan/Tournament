//package com.duan2.tournamentTDTU.fileUpload;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.scheduling.annotation.EnableAsync;
//import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
//import java.util.concurrent.Executor;
//
//@Configuration
//@EnableAsync
//public class createScheduleProgress {
//    @Bean(name = "scheduleProgress")
//    public Executor executorFileToDB(){
//        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
//        executor.setCorePoolSize(3); // Số lượng thread tối thiểu trong pool
//        executor.setMaxPoolSize(5); // Số lượng thread tối đa
//        executor.setQueueCapacity(100); // Kích thước hàng đợi
//        executor.setThreadNamePrefix("FileInsertDB-");
//        executor.initialize();
//        return executor;
//    }
//}
