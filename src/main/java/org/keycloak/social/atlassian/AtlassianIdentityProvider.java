package org.keycloak.social.atlassian;

import com.fasterxml.jackson.databind.JsonNode;
import org.jboss.logging.Logger;
import org.keycloak.broker.oidc.AbstractOAuth2IdentityProvider;
import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.broker.provider.IdentityBrokerException;
import org.keycloak.broker.provider.util.SimpleHttp;
import org.keycloak.broker.social.SocialIdentityProvider;
import org.keycloak.events.EventBuilder;
import org.keycloak.models.KeycloakSession;

import java.util.Arrays;

public class AtlassianIdentityProvider extends AbstractOAuth2IdentityProvider<AtlassianIdentityProviderConfig>
        implements SocialIdentityProvider<AtlassianIdentityProviderConfig> {

    private static final Logger log = Logger.getLogger(AtlassianIdentityProvider.class);
    public static final String AUTH_URL = "https://auth.atlassian.com/authorize";
    public static final String TOKEN_URL = "https://auth.atlassian.com/oauth/token";
    public static final String PROFILE_URL = "https://api.atlassian.com/me";
    public static final String DEFAULT_SCOPE = "read::me";

    public AtlassianIdentityProvider(KeycloakSession session, AtlassianIdentityProviderConfig config) {
        super(session, config);
        config.setAuthorizationUrl(AUTH_URL);
        config.setTokenUrl(TOKEN_URL);
        config.setUserInfoUrl(PROFILE_URL);
    }

    @Override
    protected boolean supportsExternalExchange() {
        return true;
    }

    @Override
    protected String getProfileEndpointForValidation(EventBuilder event) {
        return PROFILE_URL;
    }

    @Override
    protected BrokeredIdentityContext extractIdentityFromProfile(EventBuilder event, JsonNode profile) {
        log.debug("Extracting from Atlassian profile " + profile.toPrettyString());
        
        final String id = this.getJsonProperty(profile, "account_id");
        final BrokeredIdentityContext user = new BrokeredIdentityContext(id);
        
        final String email = this.getJsonProperty(profile, "email");
        user.setEmail(email);

        String username = this.getJsonProperty(profile, "nickname");
        if (username == null) {
            if (email != null) {
                username = Arrays.stream(email.split("@")).findFirst().orElse(id);
            } else {
                username = id;
            }
        } else {
            // nicknames can contain spaces. Convert whitespace to hyphens a non-space 
            // character. 
            username = username.replaceAll("\\s+", "-");
        }
        user.setUsername(username);

        final String name = this.getJsonProperty(profile, "name");
        //noinspection deprecation
        user.setName(name);
        
        user.setIdpConfig(this.getConfig());
        user.setIdp(this);
        AbstractJsonUserAttributeMapper.storeUserProfileForMapper(user, profile, getConfig().getAlias());
        return user;
    }

    @Override
    protected BrokeredIdentityContext doGetFederatedIdentity(String accessToken) {
        JsonNode profile;
        try {
            profile = SimpleHttp.doGet(PROFILE_URL, session).header("Authorization", "Bearer " + accessToken).asJson();
        } catch (Exception e) {
            log.error("Failed to get identity from profile URL.", e);
            throw new IdentityBrokerException("Could not obtain user profile from Atlassian.", e);
        }

        return extractIdentityFromProfile(null, profile);
    }

    @Override
    protected String getDefaultScopes() {
        return DEFAULT_SCOPE;
    }
}
