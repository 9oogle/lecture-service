package com.goggles.lecture_service.global;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;
import java.util.UUID;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class JpaAuditingConfig {

  private static final String USER_ID_HEADER = "X-User-Id";

  @Bean
  public AuditorAware<UUID> auditorAware() {
    return () -> {
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
      if (attributes == null) {
        return Optional.empty();
      }

      HttpServletRequest request = attributes.getRequest();
      String userId = request.getHeader(USER_ID_HEADER);
      if (userId == null || userId.isBlank()) {
        return Optional.empty();
      }

      try {
        return Optional.of(UUID.fromString(userId));
      } catch (IllegalArgumentException e) {
        return Optional.empty();
      }
    };
  }
}
