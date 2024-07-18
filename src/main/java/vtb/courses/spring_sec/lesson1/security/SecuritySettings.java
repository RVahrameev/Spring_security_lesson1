package vtb.courses.spring_sec.lesson1.security;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.firewall.RequestRejectedException;
import org.springframework.security.web.firewall.StrictHttpFirewall;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * SecuritySettings - осуществляет настройку Spring Security
 */
@Component
public class SecuritySettings {
    /**
     * WhiteListFilter - кастомный фильтр осуществляющий логику доступа по "Белому списку"
     * Не вынесен в отдельный файл, т.к. иначе Spring Security автоматически его цепляет и
     * начинает применять к каждой странице сайта
     */
    public class WhiteListFilter implements Filter {
        @Override
        public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
            //System.out.println("session: " + ((HttpServletRequest)servletRequest).getSession().getId());
            if  (
                servletRequest instanceof HttpServletRequest
                &&
                !urlMatcher.checkUrl(((HttpServletRequest)servletRequest).getHeader("referer"))
                )
            {
                throw new WhiteListAccessException("");
            } else {
                filterChain.doFilter(servletRequest, servletResponse);
            }
        }
    }

    /**
     * handleException - общий обработчик ошибок возникающих в фильтрах
     */
    private static void  handleException(HttpServletRequest request, HttpServletResponse response, RequestRejectedException requestRejectedException) throws IOException, ServletException {
        if (requestRejectedException instanceof WhiteListAccessException) {
            response.sendRedirect("AccessDenied.html");
        }
    }

    @Autowired
    private WhiteListMatcher urlMatcher;

//    private Pattern greetingPage = Pattern.compile(".+/greeting2$");
//    private class HttpFirewall extends StrictHttpFirewall {
//
//        @Override
//        public FirewalledRequest getFirewalledRequest(HttpServletRequest request) throws RequestRejectedException {
//            Matcher requestUrlMatcher = greetingPage.matcher(request.getRequestURL());
//            if (requestUrlMatcher.find()) {
//                if (!urlMatcher.checkUrl(request.getHeader("referer"))) {
//                    throw new WhiteListAccessException("");
//                } else {
//                    filterChain.doFilter(servletRequest, servletResponse);
//                }
//            }
//            return super.getFirewalledRequest(request);
//        }
//    }

    /**
     * initSecurity - настройка Spring Security HTTP Firewall
     */
    @Bean
    public WebSecurityCustomizer initSecurity() {

        return web -> web
                .requestRejectedHandler(SecuritySettings::handleException)
//                .httpFirewall(new HttpFirewall())
                .httpFirewall(new StrictHttpFirewall())
                ;
    }

    /**
     * filterChainWhiteList - фильтр для организации логики доступа по "Белому списку"
     */
    @Bean @Order(1)
    SecurityFilterChain filterChainWhiteList(HttpSecurity http) throws Exception{
        return http
                .securityMatcher("/greeting2/**")
                .addFilterAfter(new WhiteListFilter(), LogoutFilter.class)
                .build();
    }

    /**
     * filterChainAuthenticatedAccessOnly - фильтр для разрешения доступа к страницам сайта
     * только аутенифицированным пользователям
     * +
     * ограничение на кол-во одновременных сессий одного пользователя
     */
    @Bean @Order(2)
    SecurityFilterChain filterChainAuthenticatedAccessOnly(HttpSecurity http) throws Exception{
        return http
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.maximumSessions(1))
                .formLogin(c -> c.defaultSuccessUrl("/", true))
                .authorizeHttpRequests(c -> c.requestMatchers("/**").authenticated())
                .build();
    }

}
