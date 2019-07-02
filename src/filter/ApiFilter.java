package filter;

import beans.User;
import util.EncodeAndDecode;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class ApiFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) servletRequest;
        HttpServletResponse res = (HttpServletResponse) servletResponse;
        res.setHeader("Access-Control-Allow-Origin", "*");
        res.setHeader("Access-Control-Allow-Headers", "Authentication");
        Cookie[] cookies = req.getCookies();
        String username = null;
        String id = null;
        User user = null;
        if (cookies != null)
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("username")) username = cookie.getValue();
                else if (cookie.getName().equals("id")) id = cookie.getValue();
            }
        if (username != null && id != null)
            user = new User(EncodeAndDecode.decode(username), EncodeAndDecode.decode(id));
        if (user != null && user.isUser())
            servletRequest.setAttribute("user", user);
        servletRequest.getRequestDispatcher(req.getRequestURI().substring(req.getContextPath().length()) + ".jsp").forward(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
