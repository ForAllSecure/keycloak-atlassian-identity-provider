package org.keycloak.social.atlassian;

import org.keycloak.broker.oidc.OAuth2IdentityProviderConfig;
import org.keycloak.models.IdentityProviderModel;

public class AtlassianIdentityProviderConfig extends OAuth2IdentityProviderConfig {

    public AtlassianIdentityProviderConfig(IdentityProviderModel model) {
        super(model);
    }

    public AtlassianIdentityProviderConfig() {
    }
    
}
