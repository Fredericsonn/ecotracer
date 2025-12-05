package upec.episen.eco.controllers.User.auth;

import java.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import upec.episen.eco.models.User.User;
import upec.episen.eco.security.JwtUtil;
import upec.episen.eco.service.User.UserService;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwt;

    @Autowired
    private UserService userservice;

    @PostMapping // the request body is a class that we defined to encapsulate the login data
    public ResponseEntity<?> login(@RequestBody AuthenticationRequest authRequest) { 

        try {
            
            // we get the username 
            String username = authRequest.getUsername();

            // we launch the authentication process 
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, authRequest.getPassword()
            ));

            // we generate a jwt token
            String token = jwt.generateToken(username);

            // get the corresponding user
            User user = userservice.getUserByUsername(username);

            user.setPassword(null); // remove the password to not expose it on the web

            // the auhentication response is a class that we defined to encapsulate the user data and jwt token returned to the user
            return ResponseEntity.ok(new AuthenticationResponse(user, token));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Map.of("msg", e.getMessage()));
        }
    }
}
