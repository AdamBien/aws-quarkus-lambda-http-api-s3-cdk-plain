package airhacks.qmpd.addresses.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import jakarta.json.Json;
import jakarta.json.JsonObject;

/**
 * Response model for address operations.
 * 
 * Contains complete address information including id and timestamps
 * for successful operations. Used for API responses to provide
 * full address details to clients.
 */
public record AddressResponse(
    String id,
    String street,
    String city,
    String state,
    String postalCode,
    String country,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    
    /**
     * Creates AddressResponse from Address entity.
     */
    public static AddressResponse from(Address address) {
        return new AddressResponse(
            address.id(),
            address.street(),
            address.city(),
            address.state(),
            address.postalCode(),
            address.country(),
            address.createdAt(),
            address.updatedAt()
        );
    }
    
    public JsonObject toJSON() {
        return Json.createObjectBuilder()
            .add("id", id)
            .add("street", street)
            .add("city", city)
            .add("state", state)
            .add("postalCode", postalCode)
            .add("country", country)
            .add("createdAt", createdAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .add("updatedAt", updatedAt.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
    }
    
    public static AddressResponse fromJSON(JsonObject json) {
        return new AddressResponse(
            json.getString("id"),
            json.getString("street"),
            json.getString("city"),
            json.getString("state"),
            json.getString("postalCode"),
            json.getString("country"),
            LocalDateTime.parse(json.getString("createdAt"), DateTimeFormatter.ISO_LOCAL_DATE_TIME),
            LocalDateTime.parse(json.getString("updatedAt"), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        );
    }
}