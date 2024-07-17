package vtb.courses.spring_sec.lesson1.entity;

import org.springframework.security.web.firewall.RequestRejectedException;

public class WhiteListAccessException extends RequestRejectedException {
    public WhiteListAccessException(String message) {
        super(message);
    }
}
