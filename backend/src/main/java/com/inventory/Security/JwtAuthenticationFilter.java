package com.inventory.Security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.Claims;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // ② ログインAPIへのアクセスはトークンチェックをスキップする
        if (request.getRequestURI().equals("/products/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        // ③ それ以外の通信はヘッダーからトークンを取り出してチェックする
        String header = request.getHeader("Authorization");

        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);

            try {
                Claims claims = jwtUtil.validateToken(token);

                String sub = claims.getSubject();
                String role = claims.get("role", String.class);

                List<GrantedAuthority> authorities = new ArrayList<>();
                authorities.add(new SimpleGrantedAuthority(role));

                if (sub != null) {
                    SecurityContext context = SecurityContextHolder.createEmptyContext();
                    Authentication authentication = new UsernamePasswordAuthenticationToken(sub, null, authorities);
                    context.setAuthentication(authentication);

                    SecurityContextHolder.setContext(context);
                }
            } catch (Exception e) {
                System.out.println("❌ 無効なJWTトークンが送信されました: " + e.getMessage());
            }
        }

        filterChain.doFilter(request, response);
    }
}
