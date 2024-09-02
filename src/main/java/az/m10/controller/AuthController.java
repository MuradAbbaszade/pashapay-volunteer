package az.m10.controller;

import az.m10.domain.Volunteer;
import az.m10.dto.JwtResponse;
import az.m10.dto.SignInDTO;
import az.m10.dto.TokenRefreshRequest;
import az.m10.exception.CustomNotFoundException;
import az.m10.service.VolunteerService;
import az.m10.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:3000", "*"})
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final VolunteerService volunteerService;

    @PostMapping("/sign-in")
    public JwtResponse signIn(@RequestBody SignInDTO signInDTO) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDTO.getUsername(),
                        signInDTO.getPassword()
                )
        );
        if (signInDTO.getFcmToken() != null && !signInDTO.getFcmToken().equals("string")) {
            volunteerService.saveFcmToken(signInDTO.getUsername(), signInDTO.getFcmToken());
        }
        String profileImage;
        try {
            profileImage = volunteerService.findByUsername(signInDTO.getUsername()).getProfileImage();
        } catch (CustomNotFoundException e) {
            profileImage = null;
        }
        String refreshToken = jwtUtil.generateRefreshTokenFromUsername(signInDTO.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtUtil.generateToken(authentication);
        return new JwtResponse(token, refreshToken, profileImage);
    }

    @PostMapping("/refresh-token")
    public JwtResponse refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        jwtUtil.validateToken(requestRefreshToken);

        String username = jwtUtil.extractClaims(request.getRefreshToken()).getSubject();
        String token = jwtUtil.generateTokenFromUsername(username);
        requestRefreshToken = jwtUtil.generateRefreshTokenFromUsername(username);

        Volunteer volunteer = volunteerService.findByUsername(username);
        String refreshToken = jwtUtil.generateRefreshTokenFromUsername(username);
        return new JwtResponse(token, requestRefreshToken, volunteer.getProfileImage());
    }
}
