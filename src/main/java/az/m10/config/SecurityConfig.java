package az.m10.config;

import az.m10.auth.AuthEntryPointJwt;
import az.m10.auth.DaoAuthProvider;
import az.m10.util.JwtUtil;
import az.m10.filter.JwtAuthFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final AuthEntryPointJwt authEntryPointJwt;

    @Bean
    public SecurityFilterChain configure(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
                .exceptionHandling().authenticationEntryPoint(authEntryPointJwt).and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests((authz) -> authz
                        .antMatchers("/api/v1/auth/sign-in").permitAll()
                        .antMatchers("/api/v1/location/").hasAnyAuthority("ADMIN")
                        .antMatchers("/api/v1/location/search").hasAnyAuthority("VOLUNTEER", "ADMIN")
                        .antMatchers("/api/v1/reservation/**").hasAnyAuthority("VOLUNTEER","ADMIN")
                        .antMatchers("/api/v1/admin/**").hasAnyAuthority("ADMIN")
                        .antMatchers("/api/v1/reservation/admin/**").hasAnyAuthority("ADMIN")
                        .anyRequest().permitAll()
                )
                .addFilterBefore(authenticationTokenFilterBean(),
                        UsernamePasswordAuthenticationFilter.class)
                .httpBasic(withDefaults());
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        AuthenticationProvider authenticationProvider = new DaoAuthProvider(passwordEncoder(), userDetailsService);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtAuthFilter authenticationTokenFilterBean() throws Exception {
        return new JwtAuthFilter(jwtUtil, userDetailsService);
    }

}