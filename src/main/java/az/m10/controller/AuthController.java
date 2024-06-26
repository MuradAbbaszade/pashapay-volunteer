package az.m10.controller;

import az.m10.dto.JwtResponse;
import az.m10.dto.SignInDTO;
import az.m10.dto.TokenRefreshRequest;
import az.m10.service.VolunteerService;
import az.m10.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = {"http://localhost:3000", "*"})
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;
    private final VolunteerService volunteerService;

    @PostMapping("/sign-in")
    public JwtResponse signIn(@RequestBody SignInDTO signInDTO) {
        final Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        signInDTO.getUsername(),
                        signInDTO.getPassword()
                )
        );
        System.out.println("fcmToken:" + signInDTO.getFcmToken());
        if (signInDTO.getFcmToken() != null && !signInDTO.getFcmToken().equals("string")) {
            System.out.println("fcmToken:" + signInDTO.getFcmToken());
            volunteerService.saveFcmToken(signInDTO.getUsername(), signInDTO.getFcmToken());
        }
        String refreshToken = jwtUtil.generateRefreshTokenFromUsername(signInDTO.getUsername());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        final String token = jwtUtil.generateToken(authentication);
        return new JwtResponse(token, refreshToken);
    }

    @PostMapping("/refresh-token")
    public JwtResponse refreshToken(@RequestBody TokenRefreshRequest request) {
        String requestRefreshToken = request.getRefreshToken();
        jwtUtil.validateToken(requestRefreshToken);

        String username = jwtUtil.extractClaims(request.getRefreshToken()).getSubject();
        String token = jwtUtil.generateTokenFromUsername(username);
        requestRefreshToken = jwtUtil.generateRefreshTokenFromUsername(username);
        return new JwtResponse(token, requestRefreshToken);
    }
}
