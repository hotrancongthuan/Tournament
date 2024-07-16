package com.duan2.tournamentTDTU.fileUpload;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Service
public class UploadProcessService {
    private final ConcurrentHashMap<String, Boolean> uploadInProgress = new ConcurrentHashMap<>();

    public List<String> getAllUploadsInProgress() {
        return uploadInProgress.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }


    public boolean canThread(String userId) {
        return uploadInProgress.getOrDefault(userId, false) == false;
    }

    public void setThreadStart(String userId) {
        uploadInProgress.put(userId, true);
    }

    public void setThreadFinish(String userId) {
        uploadInProgress.put(userId, false);
    }
}
