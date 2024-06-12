package az.m10.controller;

import az.m10.dto.NotificationRequest;
import az.m10.dto.NotificationResponse;
import az.m10.service.FCMService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
public class NotificationController {
    @Autowired
    private FCMService fcmService;

    @PostMapping("/notification")
    public ResponseEntity sendNotification(@RequestBody NotificationRequest request) throws ExecutionException, InterruptedException {
        fcmService.sendMessageToToken(request);
        return ResponseEntity.ok(new NotificationResponse(HttpStatus.OK.value(), "Notification sent"));
    }

}
