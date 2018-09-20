package ro.bmariesan.techsummit.authentication;

import org.springframework.stereotype.Service;

@Service
public class EmqttAuthenticationService {

    public boolean authenticateUser(String username, String password, String clientId) {
        return username != null && password != null && clientId != null;
    }


    public boolean authorizeUser(String username, String clientId, String access, String ipaddr, String topic) {
        return username != null && access != null && clientId != null && topic != null;
    }

    public boolean checkSuperUser(String username, String clientid) {
        return false;
    }
}
