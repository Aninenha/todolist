package com.aninenha.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import com.aninenha.todolist.user.IUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {

    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        var servletPath = request.getServletPath();

        if (servletPath.startsWith("/tasks/")) {
            var authorization = request.getHeader("Authorization");

            if (authorization == null || !authorization.startsWith("Basic ")) {
                response.sendError(401, "Nao autorizado");
                return;
            }

            var authtrim = authorization.substring(6).trim();

            byte[] authdecoded;
            try {
                authdecoded = Base64.getDecoder().decode(authtrim);
            } catch (IllegalArgumentException e) {
                response.sendError(401, "Nao autorizado");
                return;
            }

            var authString = new String(authdecoded, StandardCharsets.UTF_8);
            String[] credentials = authString.split(":", 2);

            if (credentials.length < 2) {
                response.sendError(401, "Nao autorizado");
                return;
            }

            String username = credentials[0];
            String password = credentials[1];

            var user = this.userRepository.findByUsername(username);

            if (user == null) {
                response.sendError(401, "Nao autorizado");
                return;
            }

            var verifyPass = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
            if (verifyPass.verified) {
                request.setAttribute("idUser", user.getId());
                filterChain.doFilter(request, response);
            } else {
                response.sendError(401, "Nao autorizado");
            }
        } else {
            filterChain.doFilter(request, response);
        }
    }
}
