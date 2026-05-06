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

  public static final UUID SYSTEM_USER_ID = UUID.fromString("00000000-0000-0000-0000-000000000000");

  @Bean
  public AuditorAware<UUID> auditorAware() {
    return () -> {
      ServletRequestAttributes attributes =
          (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

      if (attributes == null) {
        return Optional.of(SYSTEM_USER_ID);
      }

      HttpServletRequest request = attributes.getRequest();
      String userId = request.getHeader(USER_ID_HEADER);
      if (userId == null || userId.isBlank()) {
        return Optional.of(SYSTEM_USER_ID);
      }

      try {
        return Optional.of(UUID.fromString(userId));
      } catch (IllegalArgumentException e) {
        return Optional.of(SYSTEM_USER_ID);
      }
    };
  }
}
