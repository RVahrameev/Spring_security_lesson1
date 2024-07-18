package vtb.courses.spring_sec.lesson1.security;

import org.springframework.stereotype.Component;


/**
 * WhiteUrlList - простенькая(индикативаная) реализация интерфейса проверки на вхождение в "Белый список"
 */
@Component
public class WhiteUrlList implements WhiteListMatcher {
    @Override
    public boolean checkUrl(String url) {
        return (url == null) || url.indexOf("http://localhost:8080/greeting") == 0;
    }
}
