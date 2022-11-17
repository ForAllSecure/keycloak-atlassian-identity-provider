# Example adding Atlassian Identity Provider to a Keycloak 15.X installation
FROM quay.io/keycloak/keycloak:15.1.1 as plugin-keycloak-atlassian-identity-provider

USER root

COPY . /project
RUN cd /project && ./mvnw clean package

FROM quay.io/keycloak/keycloak:15.1.1

USER jboss

## Atlassian social login support
COPY --from=plugin-keycloak-atlassian-identity-provider /project/target/*.jar /opt/jboss/keycloak/providers/keycloak-atlassian-user-provider.jar
# Keycloak < 16 will not read theme files from JAR. So we manually
# copy them into the base theme so that it is available to all other
# themes.
#
# https://github.com/BenjaminFavre/keycloak-apple-social-identity-provider/issues/20#issuecomment-1015414303
#
COPY --from=plugin-keycloak-atlassian-identity-provider /project/src/main/resources/theme-resources/resources/partials/* /opt/jboss/keycloak/themes/base/admin/resources/partials/