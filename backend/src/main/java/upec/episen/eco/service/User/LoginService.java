package upec.episen.eco.service.User;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import upec.episen.eco.models.User.User;

@Service
public class LoginService implements UserDetailsService {

    @Autowired
    private UserService userservice;

    @Override
    public UserDetails loadUserByUsername(String username) {

        try {
            User user = userservice.getUserByUsername(username);

            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(username)
                    .password(user.getPassword())
                    .authorities("USER")
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

}
