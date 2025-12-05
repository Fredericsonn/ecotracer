package upec.episen.eco;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PlayGround {
    public static void main(String[] args) {
        PasswordEncoder encoder = new BCryptPasswordEncoder();

        System.out.println(encoder.matches("KATARINA", "$2a$10$35xJ.RpCRD9uLKCc3W.q7ONmPYLvIqeHIpp6USOPlLN80f08O92c2"));
    }
}
