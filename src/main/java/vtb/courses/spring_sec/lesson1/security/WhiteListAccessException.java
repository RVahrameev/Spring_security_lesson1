package vtb.courses.spring_sec.lesson1.security;

import org.springframework.security.web.firewall.RequestRejectedException;

/**
 * WhiteListAccessException - исключение, которое должно выбрасыватся, если доступ к
 * странице с ограниченным ссылочным доступом осуществляется с адреса не входящего в "Белый список"
 */
public class WhiteListAccessException extends RequestRejectedException {
    public WhiteListAccessException(String message) {
        super(message);
    }
}
