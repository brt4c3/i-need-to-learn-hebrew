#!/bin/bash

# Start SSH service
service ssh start

# Start Zabbix agent service
service zabbix-agent start

# Start Prometheus in the background
/opt/prometheus/prometheus --config.file=/opt/prometheus/prometheus.yml &

# Start Tomcat in the foreground
/opt/tomcat/bin/startup.sh
/opt/tomcat/bin/catalina.sh run