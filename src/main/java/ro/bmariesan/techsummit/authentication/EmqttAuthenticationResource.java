package ro.bmariesan.techsummit.authentication;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mqtt")
public class EmqttAuthenticationResource {

    private static final Logger LOG = LoggerFactory.getLogger(EmqttAuthenticationResource.class);
    private final EmqttAuthenticationService emqttAuthenticationService;

    public EmqttAuthenticationResource(EmqttAuthenticationService emqttAuthenticationService) {
        this.emqttAuthenticationService = emqttAuthenticationService;
    }

    @PostMapping("/auth")
    public ResponseEntity<?> auth(@RequestParam(value = "username", required = false) String username,
                                  @RequestParam(value = "password", required = false) String password,
                                  @RequestParam(value = "clientid", required = false) String clientid) {
        boolean authenticate = emqttAuthenticationService.authenticateUser(username, password, clientid);
        LOG.info("Mqtt broker auth request for username=[{}], password=[*****] and clientid=[{}]", username, clientid);
        return new ResponseEntity<>(authenticate ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

    @PostMapping("/superuser")
    public ResponseEntity<?> superuser(@RequestParam(value = "username", required = false) String username,
                                       @RequestParam(value = "clientid", required = false) String clientid) {
        boolean authenticate = emqttAuthenticationService.checkSuperUser(username, clientid);
        LOG.info("Mqtt broker superuser request for username=[{}] and clientid=[{}]", username, clientid);
        return new ResponseEntity<>(authenticate ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }

    @PostMapping("/acl")
    public ResponseEntity<?> acl(@RequestParam(value = "username", required = false) String username,
                                 @RequestParam(value = "clientid", required = false) String clientid,
                                 @RequestParam(value = "access", required = false) String access,
                                 @RequestParam(value = "ipaddr", required = false) String ipaddr,
                                 @RequestParam(value = "topic", required = false) String topic) {
        boolean authorize = emqttAuthenticationService.authorizeUser(username, clientid, access, ipaddr, topic);
        LOG.info("Mqtt broker acl request for username=[{}], clientid=[{}], topic=[{}]", username, clientid, topic);
        return new ResponseEntity<>(authorize ? HttpStatus.OK : HttpStatus.FORBIDDEN);
    }
}
