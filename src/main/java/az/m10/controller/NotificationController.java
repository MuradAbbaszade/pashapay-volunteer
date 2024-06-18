package az.m10.controller;

import az.m10.dto.InitialRequest;
import az.m10.dto.NotificationRequest;
import az.m10.dto.NotificationResponse;
import az.m10.service.FCMService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@RestController
@RequiredArgsConstructor
public class NotificationController {
    private final FCMService fcmService;

    @PostMapping("/notification")
    public ResponseEntity sendNotification(@RequestBody NotificationRequest request) throws ExecutionException, InterruptedException {
        fcmService.sendMessageToToken(request);
        return ResponseEntity.ok(new NotificationResponse(HttpStatus.OK.value(), "Notification sent"));
    }

    @PostMapping("/send-data")
    public ResponseEntity sendData(@RequestBody InitialRequest request) {
        Map<String, String> data = new HashMap<>();
        data.put("data", request.getData());
        fcmService.sendDataMessage(request.getToken(), data);
        return ResponseEntity.ok(new NotificationResponse(HttpStatus.OK.value(), "Data sent"));
    }

}
