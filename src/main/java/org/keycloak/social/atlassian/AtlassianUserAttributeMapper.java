package org.keycloak.social.atlassian;

import org.keycloak.broker.oidc.mappers.AbstractJsonUserAttributeMapper;


public class AtlassianUserAttributeMapper extends AbstractJsonUserAttributeMapper {

	private static final String[] cp = new String[] { AtlassianIdentityProviderFactory.PROVIDER_ID };

	@Override
	public String[] getCompatibleProviders() {
		return cp;
	}

	@Override
	public String getId() {
		return "atlassian-user-attribute-mapper";
	}

}
