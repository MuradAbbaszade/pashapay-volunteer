package az.m10.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignInDTO {
    private String username;
    private String password;
    private String fcmToken;
}
