FROM quay.io/keycloak/keycloak-x:${docker.keycloak.version} AS inoa_cnpm_keycloak

ENV KC_HTTP_ENABLED=true
ENV KC_CLUSTER=local
ENV KEYCLOAK_ADMIN=admin
ENV KEYCLOAK_ADMIN_PASSWORD=password

ADD realm.json /docker/realm.json
RUN /opt/jboss/keycloak/bin/kc.sh import --file /docker/realm.json; exit 0
