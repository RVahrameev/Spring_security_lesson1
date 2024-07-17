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
import vtb.courses.spring_sec.lesson1.entity.WhiteListAccessException;
import vtb.courses.spring_sec.lesson1.entity.WhiteListMatcher;

import java.io.IOException;

//class WhiteListAccess extends RequestRejectedException {
//    public WhiteListAccess(String message) {
//        super(message);
//    }
//}

@Component
public class SecuritySettings {
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

//    private final WhiteListFilter whiteListFilter;

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
//                    throw new WhiteListAccess("");
//                } else {
//                    filterChain.doFilter(servletRequest, servletResponse);
//                }
//            }
//            return super.getFirewalledRequest(request);
//        }
//    }

    @Bean
    public WebSecurityCustomizer initSecurity() {

        return web -> web
                .requestRejectedHandler(SecuritySettings::handleException)
//                .httpFirewall(new HttpFirewall())
                .httpFirewall(new StrictHttpFirewall())
                ;
    }

    @Bean @Order(1)
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        return http
                .securityMatcher("/greeting2/**")
                .addFilterAfter(new WhiteListFilter(), LogoutFilter.class)
                .build();
    }

    @Bean @Order(2)
    SecurityFilterChain filterChain2(HttpSecurity http) throws Exception{
        return http
                .sessionManagement(httpSecuritySessionManagementConfigurer -> httpSecuritySessionManagementConfigurer.maximumSessions(1))
                .formLogin(c -> c.defaultSuccessUrl("/", true))
                .authorizeHttpRequests(c -> c.requestMatchers("/**").authenticated())
                .build();
    }

//    public SecuritySettings() {
//        whiteListFilter = new WhiteListFilter();
//    }
}
