package airhacks.qmpd.addresses.control;

import airhacks.qmpd.addresses.entity.Address;
import airhacks.qmpd.addresses.entity.AddressValidationException;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for AddressValidator business logic.
 * 
 * Tests validation rules for address creation and updates
 * following maximum 3 tests per class guideline.
 */
@QuarkusTest
class AddressValidatorTest {
    
    @Test
    @DisplayName("Valid address creation request passes validation")
    void validAddressCreationPasses() {
        var address = Address.create(
            "123 Main St",
            "Springfield",
            "IL",
            "62701",
            "US"
        );

        assertDoesNotThrow(() -> AddressValidator.validateForCreation(address));
    }

    @Test
    @DisplayName("Missing required fields trigger validation errors")
    void missingRequiredFieldsFailValidation() {
        var address = Address.create(
            null,
            "Springfield",
            null,
            "62701",
            "US"
        );

        var exception = assertThrows(AddressValidationException.class,
            () -> AddressValidator.validateForCreation(address));

        assertEquals(2, exception.getValidationErrors().size());
        assertTrue(exception.hasErrorsForField("street"));
        assertTrue(exception.hasErrorsForField("state"));
    }

    @Test
    @DisplayName("Invalid postal code format fails validation")
    void invalidPostalCodeFormatFails() {
        var address = Address.create(
            "123 Main St",
            "Springfield",
            "IL",
            "invalid-postal",
            "US"
        );

        var exception = assertThrows(AddressValidationException.class,
            () -> AddressValidator.validateForCreation(address));

        assertTrue(exception.hasErrorsForField("postalCode"));
        assertTrue(exception.getMessage().contains("Invalid postal code format"));
    }
}