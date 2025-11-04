package airhacks.qmpd.addresses.boundary;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import org.eclipse.microprofile.config.ConfigProvider;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Verifies end-to-end address management through the deployed HTTP API
 */
@QuarkusTest
class AddressesResourceIT {

    @Inject
    @RestClient
    AddressesResourceClient client;

    @BeforeAll
    static void verifyPreconditions() {
        System.out.println("testing with: ");
        var baseUri = ConfigProvider
            .getConfig()
            .getOptionalValue("base_uri/mp-rest/url", String.class);
        baseUri.ifPresentOrElse(System.out::println, ()-> System.out.println("base_uri/mp-rest/url not set"));
    }
    
    @Test
    @DisplayName("Complete address lifecycle - create, read, update, delete")
    void completeAddressLifecycle() {
        var createRequest = Json.createObjectBuilder()
            .add("street", "123 Main St")
            .add("city", "Springfield")
            .add("state", "IL")
            .add("postalCode", "62701")
            .add("country", "US")
            .build();
        
        var createResponse = client.createAddress(createRequest);
        assertThat(createResponse.getStatus()).isEqualTo(201);

        var createdAddress = createResponse.readEntity(JsonObject.class);
        var addressId = createdAddress.getString("id");
        assertThat(addressId).isNotNull();
        assertThat(createdAddress.getString("street")).isEqualTo("123 Main St");
        
        var getResponse = client.getAddress(addressId);
        assertThat(getResponse.getStatus()).isEqualTo(200);

        var retrievedAddress = getResponse.readEntity(JsonObject.class);
        assertThat(retrievedAddress.getString("id")).isEqualTo(addressId);
        assertThat(retrievedAddress.getString("city")).isEqualTo("Springfield");
        
        // Update address
        var updateRequest = Json.createObjectBuilder()
            .add("city", "Chicago")
            .add("postalCode", "60601")
            .build();
        
        var updateResponse = client.updateAddress(addressId, updateRequest);
        assertThat(updateResponse.getStatus()).isEqualTo(200);

        var updatedAddress = updateResponse.readEntity(JsonObject.class);
        assertThat(updatedAddress.getString("city")).isEqualTo("Chicago");
        assertThat(updatedAddress.getString("postalCode")).isEqualTo("60601");
        assertThat(updatedAddress.getString("street")).isEqualTo("123 Main St");
        
        var deleteResponse = client.deleteAddress(addressId);
        assertThat(deleteResponse.getStatus()).isEqualTo(204);

        var getAfterDeleteResponse = client.getAddress(addressId);
        assertThat(getAfterDeleteResponse.getStatus()).isEqualTo(404);
    }
    
    @Test
    @DisplayName("Validation errors return proper error responses")
    void validationErrorHandling() {
        // Test missing required fields
        var invalidRequest = Json.createObjectBuilder()
            .add("street", "123 Main St")
            // Missing city, state, postalCode, country
            .build();
        
        var response = client.createAddress(invalidRequest);
        assertThat(response.getStatus()).isEqualTo(400);

        var errorResponse = response.readEntity(JsonObject.class);
        assertThat(errorResponse.getString("error")).isEqualTo("VALIDATION_ERROR");
        assertThat(errorResponse.getString("message")).isEqualTo("Address validation failed");
        assertThat(errorResponse.getJsonArray("details")).isNotEmpty();
        
        // Test invalid postal code format
        var invalidPostalRequest = Json.createObjectBuilder()
            .add("street", "123 Main St")
            .add("city", "Springfield")
            .add("state", "IL")
            .add("postalCode", "invalid")
            .add("country", "US")
            .build();
        
        var postalResponse = client.createAddress(invalidPostalRequest);
        assertThat(postalResponse.getStatus()).isEqualTo(400);

        var postalErrorResponse = postalResponse.readEntity(JsonObject.class);
        assertThat(postalErrorResponse.getString("error")).isEqualTo("VALIDATION_ERROR");
    }
    
}