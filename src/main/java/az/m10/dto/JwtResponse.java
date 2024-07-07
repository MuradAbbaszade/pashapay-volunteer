package az.m10.dto;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class JwtResponse {
    private final String TYPE = "Bearer";
    private String accessToken;
    private String refreshToken;
    private String profileImageUrl;

    public JwtResponse(String accessToken, String refreshToken, String profileImageUrl) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.profileImageUrl = profileImageUrl;
    }
}
