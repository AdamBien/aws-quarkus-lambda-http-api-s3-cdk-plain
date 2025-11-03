package airhacks.qmpd.addresses.entity;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.json.Json;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AddressUpdateRequest JSON serialization.
 *
 * Verifies partial update support where only specified fields
 * are included in JSON representation.
 */
@QuarkusTest
class AddressUpdateRequestTest {

    @Test
    @DisplayName("Partial update request serializes only non-null fields")
    void partialUpdateSerialization() {
        var request = new AddressUpdateRequest(
            "456 Oak Ave",
            null,
            "CA",
            null,
            null
        );

        var json = request.toJSON();

        assertThat(json.keySet()).containsExactlyInAnyOrder("street", "state");
        assertThat(json.getString("street")).isEqualTo("456 Oak Ave");
        assertThat(json.getString("state")).isEqualTo("CA");
    }

    @Test
    @DisplayName("Full update request serializes all fields")
    void fullUpdateSerialization() {
        var request = new AddressUpdateRequest(
            "789 Elm St",
            "Portland",
            "OR",
            "97201",
            "USA"
        );

        var json = request.toJSON();

        assertThat(json.keySet()).containsExactlyInAnyOrder(
            "street", "city", "state", "postalCode", "country"
        );
        assertThat(json.getString("street")).isEqualTo("789 Elm St");
        assertThat(json.getString("city")).isEqualTo("Portland");
    }

    @Test
    @DisplayName("Deserializes partial JSON to update request with null fields")
    void partialJSONDeserialization() {
        var json = Json.createObjectBuilder()
            .add("city", "Seattle")
            .add("postalCode", "98101")
            .build();

        var request = AddressUpdateRequest.fromJSON(json);

        assertThat(request.street()).isNull();
        assertThat(request.city()).isEqualTo("Seattle");
        assertThat(request.state()).isNull();
        assertThat(request.postalCode()).isEqualTo("98101");
        assertThat(request.country()).isNull();
    }
}
