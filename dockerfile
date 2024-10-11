# Use the Ubuntu 20.04 base image
FROM ubuntu:20.04

# Set environment variables to minimize user interaction
ENV DEBIAN_FRONTEND=noninteractive
ENV PROMETHEUS_VERSION=2.46.0
ENV TOMCAT_VERSION=9.0

# Update and install required packages
RUN apt-get update && apt-get install -y \
    build-essential \
    libffi-dev \
    python3-dev \
    qemu-user-static \
    curl \
    git \
    vim \
    wget \
    ssh \
    ca-certificates \
    openssh-server \
    sudo \
    iproute2 \
    python3-pip \
    python3-venv \
    binfmt-support \
    libxml2 \
    zabbix-agent \
    && apt-get clean \
    && rm -rf /var/lib/apt/lists/*

# Download and install Prometheus
RUN wget https://github.com/prometheus/prometheus/releases/download/v${PROMETHEUS_VERSION}/prometheus-${PROMETHEUS_VERSION}.linux-amd64.tar.gz && \
    tar -xvzf prometheus-${PROMETHEUS_VERSION}.linux-amd64.tar.gz && \
    mv prometheus-${PROMETHEUS_VERSION}.linux-amd64 /opt/prometheus && \
    ln -s /opt/prometheus/prometheus /usr/local/bin/prometheus && \
    ln -s /opt/prometheus/promtool /usr/local/bin/promtool && \
    rm prometheus-${PROMETHEUS_VERSION}.linux-amd64.tar.gz

# Download and install Apache Tomcat
RUN wget https://downloads.apache.org/tomcat/tomcat-9/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    tar -xvzf apache-tomcat-${TOMCAT_VERSION}.tar.gz && \
    mv apache-tomcat-${TOMCAT_VERSION} /opt/tomcat && \
    rm apache-tomcat-${TOMCAT_VERSION}.tar.gz

# Create a user for ansible to use
RUN useradd -m -s /bin/bash ansible_user && \
    echo "ansible_user:ansible_password" | chpasswd && \
    adduser ansible_user sudo

# Enable password authentication in SSH
RUN sed -i 's/#PasswordAuthentication yes/PasswordAuthentication yes/' /etc/ssh/sshd_config

# Configure Zabbix Agent (replace <ZABBIX_SERVER_IP> with your Zabbix server's IP address)
RUN sed -i 's/Server=127.0.0.1/Server=<ZABBIX_SERVER_IP>/' /etc/zabbix/zabbix_agentd.conf && \
    sed -i 's/Hostname=Zabbix server/Hostname=docker-agent/' /etc/zabbix/zabbix_agentd.conf

# Enable Zabbix agent service
RUN systemctl enable zabbix-agent

# Expose SSH, Zabbix agent, Prometheus, Tomcat ports
EXPOSE 22 10050 9090 8080

# Copy Prometheus configuration file
COPY prometheus.yml /opt/prometheus/prometheus.yml

# Copy entrypoint script for container startup
COPY entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/entrypoint.sh

# Start SSH, Zabbix agent, Prometheus, Tomcat, and keep the container running
CMD ["/usr/local/bin/entrypoint.sh"]