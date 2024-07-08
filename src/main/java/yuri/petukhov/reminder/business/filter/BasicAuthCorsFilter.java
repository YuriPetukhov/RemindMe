//package yuri.petukhov.reminder.business.filter;
//
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import org.springframework.stereotype.Component;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//
//@Component
//public class BasicAuthCorsFilter extends OncePerRequestFilter {
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest httpServletRequest,
//                                    HttpServletResponse httpServletResponse,
//                                    FilterChain filterChain)
//            throws ServletException, IOException {
//        httpServletResponse.addHeader("Access-Control-Allow-Credentials", "true");
//        filterChain.doFilter(httpServletRequest, httpServletResponse);
//    }
//}
//
