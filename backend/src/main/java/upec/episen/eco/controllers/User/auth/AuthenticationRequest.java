package upec.episen.eco.controllers.User.auth;

public class AuthenticationRequest {

    private String username;

    private String password;

    // Constructors
    public AuthenticationRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public AuthenticationRequest() {

    }

    // Getters - Setters
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "AuthenticationRequest [username=" + username + ", password=" + password + "]";
    }

    
}
