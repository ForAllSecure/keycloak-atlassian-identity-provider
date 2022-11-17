# Example adding Atlassian Identity Provider to a Keycloak 15.X installation
FROM quay.io/keycloak/keycloak:15.1.1 as plugin-keycloak-atlassian-identity-provider

USER root

COPY . /project
RUN cd /project && ./mvnw clean package

FROM quay.io/keycloak/keycloak:20.0

## Atlassian social login support
COPY --from=plugin-keycloak-atlassian-identity-provider /project/target/*.jar /opt/keycloak/providers/keycloak-atlassian-user-provider.jar