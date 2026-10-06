package dev.portfolio.infrastructure;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@Order(-110)
public class RateLimitFilter extends OncePerRequestFilter {
  private long window = System.nanoTime();
  private int used;

  private synchronized boolean acquire() {
    long now = System.nanoTime();
    if (now - window >= 1_000_000_000L) {
      window = now;
      used = 0;
    }
    return ++used <= 20;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith("/api/");
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    if (!acquire()) {
      response.setStatus(429);
      response.setHeader("Retry-After", "1");
      response.setContentType("application/problem+json");
      response.getWriter().write("{\"title\":\"Rate limit exceeded\",\"status\":429}");
      return;
    }
    chain.doFilter(request, response);
  }
}
