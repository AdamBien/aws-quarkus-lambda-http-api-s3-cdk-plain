package airhacks.qmpd.addresses.entity;

import jakarta.json.Json;
import jakarta.json.JsonObject;

/**
 * Request model for updating existing address records.
 * 
 * Contains optional fields for partial updates where only
 * specified fields are modified. All fields are nullable
 * to support partial update scenarios.
 */
public record AddressUpdateRequest(
    String street,
    String city,
    String state,
    String postalCode,
    String country
) {
    
    public JsonObject toJSON() {
        var builder = Json.createObjectBuilder();
        
        if (street != null) {
            builder.add("street", street);
        }
        if (city != null) {
            builder.add("city", city);
        }
        if (state != null) {
            builder.add("state", state);
        }
        if (postalCode != null) {
            builder.add("postalCode", postalCode);
        }
        if (country != null) {
            builder.add("country", country);
        }
        
        return builder.build();
    }
    
    public static AddressUpdateRequest fromJSON(JsonObject json) {
        return new AddressUpdateRequest(
            json.containsKey("street") ? json.getString("street") : null,
            json.containsKey("city") ? json.getString("city") : null,
            json.containsKey("state") ? json.getString("state") : null,
            json.containsKey("postalCode") ? json.getString("postalCode") : null,
            json.containsKey("country") ? json.getString("country") : null
        );
    }
}