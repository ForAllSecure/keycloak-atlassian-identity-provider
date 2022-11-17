package org.keycloak.social.atlassian;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.keycloak.broker.provider.BrokeredIdentityContext;
import org.keycloak.models.KeycloakSession;
import org.mockito.Mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link AtlassianIdentityProvider}
 */
public class AtlassianIdentityProviderTest {

    // Used to deserialize string to JsonNode
    private final ObjectMapper mapper = new ObjectMapper();
    
    @Mock
    private KeycloakSession session;

    private AtlassianIdentityProvider idp;

    @Before
    public void setup() {
        final AtlassianIdentityProviderConfig config = new AtlassianIdentityProviderConfig();
        idp = new AtlassianIdentityProvider(session, config);
    }

    @Test
    public void extractIdentityFromProfile() throws JsonProcessingException {
        final JsonNode profile = mapper.readTree("{\n" +
                "  \"account_type\": \"atlassian\",\n" +
                "  \"account_id\": \"112233aa-bb11-cc22-33dd-445566abcabc\",\n" +
                "  \"email\": \"mia@example.com\",\n" +
                "  \"name\": \"Mia Krystof\",\n" +
                "  \"picture\": \"https://avatar-management--avatars.us-west-2.prod.public.atl-paas.net/112233aa-bb11-cc22-33dd-445566abcabc/1234abcd-9876-54aa-33aa-1234dfsade9487ds\",\n" +
                "  \"account_status\": \"active\",\n" +
                "  \"nickname\": \"mkrystof\",\n" +
                "  \"zoneinfo\": \"Australia/Sydney\",\n" +
                "  \"locale\": \"en-US\",\n" +
                "  \"extended_profile\": {\n" +
                "    \"job_title\": \"Designer\",\n" +
                "    \"organization\": \"mia@example.com\",\n" +
                "    \"department\": \"Design team\",\n" +
                "    \"location\": \"Sydney\"\n" +
                "  }\n" +
                "}");
        
        final BrokeredIdentityContext user = idp.extractIdentityFromProfile(null, profile);
        assertNotNull(user);
        assertEquals(user.getId(), "112233aa-bb11-cc22-33dd-445566abcabc");
        assertEquals(user.getEmail(), "mia@example.com");
        assertEquals(user.getFirstName(), "Mia");
        assertEquals(user.getLastName(), "Krystof");
        assertEquals(user.getUsername(), "mkrystof");
        assertEquals(user.getIdp(), idp);
    }

    @Test
    public void extractIdentityFromProfile_nickname_has_spaces() throws JsonProcessingException {
        final JsonNode profile = mapper.readTree("{\n" +
                "  \"account_type\": \"atlassian\",\n" +
                "  \"account_id\": \"112233aa-bb11-cc22-33dd-445566abcabc\",\n" +
                "  \"email\": \"mia@example.com\",\n" +
                "  \"name\": \"Mia Krystof\",\n" +
                "  \"picture\": \"https://avatar-management--avatars.us-west-2.prod.public.atl-paas.net/112233aa-bb11-cc22-33dd-445566abcabc/1234abcd-9876-54aa-33aa-1234dfsade9487ds\",\n" +
                "  \"account_status\": \"active\",\n" +
                "  \"nickname\": \"this is my       nickname\",\n" +
                "  \"zoneinfo\": \"Australia/Sydney\",\n" +
                "  \"locale\": \"en-US\",\n" +
                "  \"extended_profile\": {\n" +
                "    \"job_title\": \"Designer\",\n" +
                "    \"organization\": \"mia@example.com\",\n" +
                "    \"department\": \"Design team\",\n" +
                "    \"location\": \"Sydney\"\n" +
                "  }\n" +
                "}");

        final BrokeredIdentityContext user = idp.extractIdentityFromProfile(null, profile);
        assertEquals(user.getUsername(), "this-is-my-nickname");
    }

    @Test
    public void extractIdentityFromProfile_username_uses_email_prefix_if_no_nickname() throws JsonProcessingException {
        final JsonNode profile = mapper.readTree("{\n" +
                "  \"account_type\": \"atlassian\",\n" +
                "  \"account_id\": \"112233aa-bb11-cc22-33dd-445566abcabc\",\n" +
                "  \"email\": \"mia@example.com\",\n" +
                "  \"name\": \"Mia Krystof\",\n" +
                "  \"picture\": \"https://avatar-management--avatars.us-west-2.prod.public.atl-paas.net/112233aa-bb11-cc22-33dd-445566abcabc/1234abcd-9876-54aa-33aa-1234dfsade9487ds\",\n" +
                "  \"account_status\": \"active\",\n" +
                "  \"zoneinfo\": \"Australia/Sydney\",\n" +
                "  \"locale\": \"en-US\",\n" +
                "  \"extended_profile\": {\n" +
                "    \"job_title\": \"Designer\",\n" +
                "    \"organization\": \"mia@example.com\",\n" +
                "    \"department\": \"Design team\",\n" +
                "    \"location\": \"Sydney\"\n" +
                "  }\n" +
                "}");

        final BrokeredIdentityContext user = idp.extractIdentityFromProfile(null, profile);
        assertEquals(user.getUsername(), "mia");
    }
}