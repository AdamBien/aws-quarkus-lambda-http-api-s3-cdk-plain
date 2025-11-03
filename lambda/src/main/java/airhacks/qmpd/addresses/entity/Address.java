package airhacks.qmpd.addresses.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;
import jakarta.json.Json;
import jakarta.json.JsonObject;

/**
 * Core domain object representing an address record.
 * 
 * Immutable address entity containing all address fields
 * with id and timestamp information.
 */
public record Address(
    String id,
    String street,
    String city,
    String state,
    String postalCode,
    String country,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {

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
    
    public static Address fromJSON(JsonObject json) {
        return new Address(
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

    public static Address create(String street, String city, String state, String postalCode, String country) {
        var now = LocalDateTime.now();
        return new Address(
            UUID.randomUUID().toString(),
            street,
            city,
            state,
            postalCode,
            country,
            now,
            now
        );
    }

    public static Address fromCreateRequest(JsonObject json) {
        return create(
            json.containsKey("street") ? json.getString("street") : null,
            json.containsKey("city") ? json.getString("city") : null,
            json.containsKey("state") ? json.getString("state") : null,
            json.containsKey("postalCode") ? json.getString("postalCode") : null,
            json.containsKey("country") ? json.getString("country") : null
        );
    }

    public Address update(AddressUpdateRequest request) {
        var now = LocalDateTime.now();
        return new Address(
            id,
            request.street() != null ? request.street() : street,
            request.city() != null ? request.city() : city,
            request.state() != null ? request.state() : state,
            request.postalCode() != null ? request.postalCode() : postalCode,
            request.country() != null ? request.country() : country,
            createdAt,
            now
        );
    }
}