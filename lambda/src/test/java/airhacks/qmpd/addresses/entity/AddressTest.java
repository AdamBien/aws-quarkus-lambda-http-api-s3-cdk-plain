package airhacks.qmpd.addresses.entity;

import io.quarkus.test.junit.QuarkusTest;
import jakarta.json.Json;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Address JSON deserialization.
 *
 * Verifies that missing fields are handled gracefully
 * allowing validation to catch issues rather than throwing NPE.
 */
@QuarkusTest
class AddressTest {

    @Test
    @DisplayName("Create request with missing fields returns Address with null values")
    void missingFieldsHandledGracefully() {
        var json = Json.createObjectBuilder()
            .add("street", "123 Main St")
            .build();

        var address = Address.fromCreateRequest(json);

        assertThat(address.street()).isEqualTo("123 Main St");
        assertThat(address.city()).isNull();
        assertThat(address.state()).isNull();
        assertThat(address.postalCode()).isNull();
        assertThat(address.country()).isNull();
    }

    @Test
    @DisplayName("Create request with all fields returns complete Address")
    void completeRequestCreatesAddress() {
        var json = Json.createObjectBuilder()
            .add("street", "789 Oak Ave")
            .add("city", "Boston")
            .add("state", "MA")
            .add("postalCode", "02101")
            .add("country", "USA")
            .build();

        var address = Address.fromCreateRequest(json);

        assertThat(address.street()).isEqualTo("789 Oak Ave");
        assertThat(address.city()).isEqualTo("Boston");
        assertThat(address.state()).isEqualTo("MA");
        assertThat(address.postalCode()).isEqualTo("02101");
        assertThat(address.country()).isEqualTo("USA");
        assertThat(address.id()).isNotNull();
        assertThat(address.createdAt()).isNotNull();
    }

    @Test
    @DisplayName("JSON serialization and deserialization preserves all fields")
    void jsonRoundTripPreservesData() {
        var original = Address.create("456 Elm St", "Chicago", "IL", "60601", "USA");
        
        var json = original.toJSON();
        var deserialized = Address.fromJSON(json);
        
        assertThat(deserialized.id()).isEqualTo(original.id());
        assertThat(deserialized.street()).isEqualTo(original.street());
        assertThat(deserialized.city()).isEqualTo(original.city());
        assertThat(deserialized.state()).isEqualTo(original.state());
        assertThat(deserialized.postalCode()).isEqualTo(original.postalCode());
        assertThat(deserialized.country()).isEqualTo(original.country());
        assertThat(deserialized.createdAt()).isEqualTo(original.createdAt());
        assertThat(deserialized.updatedAt()).isEqualTo(original.updatedAt());
    }
}
