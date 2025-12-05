package upec.episen.eco.controllers.User.auth;

import upec.episen.eco.models.User.User;

public class AuthenticationResponse {

    private User user;

    private String token;

    public AuthenticationResponse(User user, String token) {
        this.user = user;
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    @Override
    public String toString() {
        return "AuthenticationResponse [user=" + user + ", token=" + token + "]";
    }

    

}
