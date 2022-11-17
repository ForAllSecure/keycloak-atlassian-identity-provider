package org.keycloak.social.atlassian;

import org.keycloak.broker.provider.AbstractIdentityProviderFactory;
import org.keycloak.broker.social.SocialIdentityProviderFactory;
import org.keycloak.models.IdentityProviderModel;
import org.keycloak.models.KeycloakSession;

public class AtlassianIdentityProviderFactory extends AbstractIdentityProviderFactory<AtlassianIdentityProvider>
        implements SocialIdentityProviderFactory<AtlassianIdentityProvider> {

    public static final String PROVIDER_ID = "atlassian";

    @Override
    public String getName() {
        return "Atlassian";
    }

    @Override
    public AtlassianIdentityProvider create(KeycloakSession session, IdentityProviderModel model) {
        return new AtlassianIdentityProvider(session, new AtlassianIdentityProviderConfig(model));
    }

    @Override
    public AtlassianIdentityProviderConfig createConfig() {
        return new AtlassianIdentityProviderConfig();
    }

    @Override
    public String getId() {
        return PROVIDER_ID;
    }
}