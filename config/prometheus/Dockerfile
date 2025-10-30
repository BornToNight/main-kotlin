FROM prom/prometheus

ARG SPRING_USER
ARG SPRING_PASSWORD

COPY prometheus.yml /etc/prometheus/prometheus.yml

USER root

# EXPLAIN_V замена строк из аргумента
RUN sed -i -e 's/\${SPRING_USER}/'$SPRING_USER'/g' -e 's/\${SPRING_PASSWORD}/'$SPRING_PASSWORD'/g' /etc/prometheus/prometheus.yml