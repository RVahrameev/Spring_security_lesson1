package vtb.courses.spring_sec.lesson1.entity;

import org.springframework.stereotype.Component;

@Component
public class WhiteUrlList implements WhiteListMatcher {
    @Override
    public boolean checkUrl(String url) {
        return (url != null) && url.equals("http://localhost:8080/greeting");
    }
}
