package az.m10.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
@Setter
@Getter
public class NotificationRequest {
    private String title;

    private String body;

    private String topic;

    private String token;
}
