# Dockerfile for ID Card Manager Application
# Uses pre-built JAR from local Maven build (mvn clean package)
# Runtime: JDK 25 (eclipse-temurin:25-jdk)

# ============ RUNTIME STAGE ============
FROM eclipse-temurin:25-jdk-jammy

# Install required packages
RUN apt-get update && apt-get install -y \
    nginx \
    openssh-server \
    openssh-client \
    curl \
    supervisor \
    php-cli\
    && rm -rf /var/lib/apt/lists/*

# Configure SSH
RUN mkdir -p /run/sshd /var/log/supervisor && \
    echo "root:root" | chpasswd && \
    sed -i 's/#PermitRootLogin prohibit-password/PermitRootLogin yes/' /etc/ssh/sshd_config && \
    sed -i 's/^#Port 22/Port 22/' /etc/ssh/sshd_config

# Copy built JAR from local target
COPY target/demo-0.0.1-SNAPSHOT.jar /app/demo.jar

# Copy NGINX configuration
COPY docker/nginx.conf /etc/nginx/nginx.conf

# Copy supervisor configuration
COPY docker/supervisord.conf /etc/supervisor/conf.d/supervisord.conf

# Create application directories
RUN mkdir -p /app/uploads /app/logs && \
    chmod 755 /app

WORKDIR /app

# Remove default NGINX config
RUN rm -f /etc/nginx/sites-enabled/default

# Expose ports
# 8080 - Web (NGINX proxy)
# 8443 - HTTPS
# 2222 - SSH
EXPOSE 8080 8443 2222

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8080/profiles 2>/dev/null || exit 1

# Start supervisor (manages NGINX, SSH, and Spring Boot)
CMD ["/usr/bin/supervisord", "-c", "/etc/supervisor/conf.d/supervisord.conf"]
