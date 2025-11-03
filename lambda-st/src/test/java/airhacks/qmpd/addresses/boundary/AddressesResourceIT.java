package airhacks.qmpd.addresses.boundary;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.json.Json;
import org.eclipse.microprofile.rest.client.inject.RestClient;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * System integration tests for AddressesResource.
 * 
 * Tests complete CRUD workflows and error scenarios using
 * MicroProfile REST Client following two-module architecture.
 * Limited to 3 tests per MicroProfile guidelines.
 */
@QuarkusTest
class AddressesResourceIT {
    
    @Inject
    @RestClient
    AddressesResourceClient client;
    
    @Test
    @DisplayName("Complete address lifecycle - create, read, update, delete")
    void completeAddressLifecycle() {
        // Create address
        var createRequest = Json.createObjectBuilder()
            .add("street", "123 Main St")
            .add("city", "Springfield")
            .add("state", "IL")
            .add("postalCode", "62701")
            .add("country", "US")
            .build();
        
        var createResponse = client.createAddress(createRequest);
        assertEquals(201, createResponse.getStatus());
        
        var createdAddress = createResponse.readEntity(jakarta.json.JsonObject.class);
        var addressId = createdAddress.getString("id");
        assertNotNull(addressId);
        assertEquals("123 Main St", createdAddress.getString("street"));
        
        // Read address
        var getResponse = client.getAddress(addressId);
        assertEquals(200, getResponse.getStatus());
        
        var retrievedAddress = getResponse.readEntity(jakarta.json.JsonObject.class);
        assertEquals(addressId, retrievedAddress.getString("id"));
        assertEquals("Springfield", retrievedAddress.getString("city"));
        
        // Update address
        var updateRequest = Json.createObjectBuilder()
            .add("city", "Chicago")
            .add("postalCode", "60601")
            .build();
        
        var updateResponse = client.updateAddress(addressId, updateRequest);
        assertEquals(200, updateResponse.getStatus());
        
        var updatedAddress = updateResponse.readEntity(jakarta.json.JsonObject.class);
        assertEquals("Chicago", updatedAddress.getString("city"));
        assertEquals("60601", updatedAddress.getString("postalCode"));
        assertEquals("123 Main St", updatedAddress.getString("street")); // Unchanged
        
        // Delete address
        var deleteResponse = client.deleteAddress(addressId);
        assertEquals(204, deleteResponse.getStatus());
        
        // Verify deletion
        var getAfterDeleteResponse = client.getAddress(addressId);
        assertEquals(404, getAfterDeleteResponse.getStatus());
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
        assertEquals(400, response.getStatus());
        
        var errorResponse = response.readEntity(jakarta.json.JsonObject.class);
        assertEquals("VALIDATION_ERROR", errorResponse.getString("error"));
        assertEquals("Address validation failed", errorResponse.getString("message"));
        assertFalse(errorResponse.getJsonArray("details").isEmpty());
        
        // Test invalid postal code format
        var invalidPostalRequest = Json.createObjectBuilder()
            .add("street", "123 Main St")
            .add("city", "Springfield")
            .add("state", "IL")
            .add("postalCode", "invalid")
            .add("country", "US")
            .build();
        
        var postalResponse = client.createAddress(invalidPostalRequest);
        assertEquals(400, postalResponse.getStatus());
        
        var postalErrorResponse = postalResponse.readEntity(jakarta.json.JsonObject.class);
        assertEquals("VALIDATION_ERROR", postalErrorResponse.getString("error"));
    }
    
}