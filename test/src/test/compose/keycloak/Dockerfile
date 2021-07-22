FROM quay.io/keycloak/keycloak-x:${version.org.keycloak} AS elco_keycloak

ENV KC_HTTP_ENABLED=true
ENV KC_CLUSTER=local

ADD realm.json /docker/realm.json
RUN /opt/jboss/keycloak/bin/kc.sh import --file /docker/realm.json; exit 0
