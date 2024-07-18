package vtb.courses.spring_sec.lesson1.security;

/**
 * WhiteListMatcher - объявление интерфейса проверки на вхождение URL в "Белый список"
 */
public interface WhiteListMatcher {
    boolean checkUrl(String url);
}
