#!/bin/bash
# Start the SSH service
service ssh start

# Start the Zabbix agent service
service zabbix-agent start

# Start Prometheus
prometheus --config.file=/opt/prometheus/prometheus.yml &

/opt/tomcat/bin/catalina.sh run &

# Keep the container running by tailing syslog or another log
tail -f /var/log/syslog