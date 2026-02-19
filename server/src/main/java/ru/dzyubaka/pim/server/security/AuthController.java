package ru.dzyubaka.pim.server.security;

import io.jsonwebtoken.Jwts;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@SecurityRequirements
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationConfiguration authenticationConfiguration;

    @PostMapping("/auth")
    public String signIn(String username, String password) {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, password);
        authenticationConfiguration.getAuthenticationManager().authenticate(authentication);
        return Jwts.builder().subject(username).compact();
    }
}
