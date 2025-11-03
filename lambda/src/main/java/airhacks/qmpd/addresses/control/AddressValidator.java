package airhacks.qmpd.addresses.control;

import airhacks.qmpd.addresses.entity.Address;
import airhacks.qmpd.addresses.entity.AddressUpdateRequest;
import airhacks.qmpd.addresses.entity.AddressValidationException;
import airhacks.qmpd.addresses.entity.AddressValidationException.ValidationError;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Validator for address data and business rules.
 * 
 * Handles validation for required fields, format validation,
 * and country-specific postal code patterns for both
 * creation and update scenarios.
 */
public interface AddressValidator {
    
    // Country-specific postal code patterns
    Pattern US_POSTAL_CODE = Pattern.compile("^\\d{5}(-\\d{4})?$");
    Pattern CA_POSTAL_CODE = Pattern.compile("^[A-Z]\\d[A-Z] \\d[A-Z]\\d$");
    Pattern UK_POSTAL_CODE = Pattern.compile("^[A-Z]{1,2}\\d[A-Z\\d]? \\d[A-Z]{2}$");
    Pattern DE_POSTAL_CODE = Pattern.compile("^\\d{5}$");
    Pattern FR_POSTAL_CODE = Pattern.compile("^\\d{5}$");
    
    /**
     * Validates address data for creation.
     *
     * Ensures all required fields are present and valid,
     * including country-specific postal code format validation.
     *
     * @param address the address to validate
     * @throws AddressValidationException if validation fails
     */
    static void validateForCreation(Address address) {
        var errors = new ArrayList<ValidationError>();

        if (address == null) {
            throw new AddressValidationException("request", "Address is required");
        }

        validateRequiredField("street", address.street(), errors);
        validateRequiredField("city", address.city(), errors);
        validateRequiredField("state", address.state(), errors);
        validateRequiredField("postalCode", address.postalCode(), errors);
        validateRequiredField("country", address.country(), errors);

        if (address.street() != null && address.street().length() > 255) {
            errors.add(new ValidationError("street", "Street must not exceed 255 characters"));
        }

        if (address.city() != null && address.city().length() > 100) {
            errors.add(new ValidationError("city", "City must not exceed 100 characters"));
        }

        if (address.state() != null && address.state().length() > 50) {
            errors.add(new ValidationError("state", "State must not exceed 50 characters"));
        }

        if (address.postalCode() != null && address.country() != null) {
            validatePostalCode(address.postalCode(), address.country(), errors);
        }

        if (!errors.isEmpty()) {
            throw new AddressValidationException(errors);
        }
    }
    
    /**
     * Validates address data for updates.
     * 
     * Validates only the fields that are being updated (non-null fields).
     * At least one field must be provided for update.
     * 
     * @param request the address update request to validate
     * @throws AddressValidationException if validation fails
     */
    static void validateForUpdate(AddressUpdateRequest request) {
        var errors = new ArrayList<ValidationError>();
        
        if (request == null) {
            throw new AddressValidationException("request", "Address update request is required");
        }
        
        // Check if at least one field is provided for update
        if (request.street() == null && request.city() == null && 
            request.state() == null && request.postalCode() == null && 
            request.country() == null) {
            throw new AddressValidationException("request", "At least one field must be provided for update");
        }
        
        // Validate non-null fields
        if (request.street() != null) {
            if (request.street().trim().isEmpty()) {
                errors.add(new ValidationError("street", "Street cannot be empty"));
            } else if (request.street().length() > 255) {
                errors.add(new ValidationError("street", "Street must not exceed 255 characters"));
            }
        }
        
        if (request.city() != null) {
            if (request.city().trim().isEmpty()) {
                errors.add(new ValidationError("city", "City cannot be empty"));
            } else if (request.city().length() > 100) {
                errors.add(new ValidationError("city", "City must not exceed 100 characters"));
            }
        }
        
        if (request.state() != null) {
            if (request.state().trim().isEmpty()) {
                errors.add(new ValidationError("state", "State cannot be empty"));
            } else if (request.state().length() > 50) {
                errors.add(new ValidationError("state", "State must not exceed 50 characters"));
            }
        }
        
        if (request.postalCode() != null) {
            if (request.postalCode().trim().isEmpty()) {
                errors.add(new ValidationError("postalCode", "Postal code cannot be empty"));
            }
            // Note: For updates, we validate postal code format only if country is also provided
            // Otherwise, the validation will be done with the existing country during processing
        }
        
        if (request.country() != null && request.country().trim().isEmpty()) {
            errors.add(new ValidationError("country", "Country cannot be empty"));
        }
        
        // If both postal code and country are being updated, validate format
        if (request.postalCode() != null && request.country() != null) {
            validatePostalCode(request.postalCode(), request.country(), errors);
        }
        
        if (!errors.isEmpty()) {
            throw new AddressValidationException(errors);
        }
    }
    
    /**
     * Validates postal code format for specific country.
     * 
     * Checks postal code against country-specific patterns
     * and adds validation error if format is invalid.
     * 
     * @param postalCode the postal code to validate
     * @param country the country code (ISO 3166-1 alpha-2)
     * @param errors list to add validation errors to
     */
    private static void validatePostalCode(String postalCode, String country, List<ValidationError> errors) {
        if (postalCode == null || country == null) {
            return;
        }
        
        var upperPostalCode = postalCode.toUpperCase().trim();
        var upperCountry = country.toUpperCase().trim();
        
        var isValid = switch (upperCountry) {
            case "US" -> US_POSTAL_CODE.matcher(upperPostalCode).matches();
            case "CA" -> CA_POSTAL_CODE.matcher(upperPostalCode).matches();
            case "GB", "UK" -> UK_POSTAL_CODE.matcher(upperPostalCode).matches();
            case "DE" -> DE_POSTAL_CODE.matcher(upperPostalCode).matches();
            case "FR" -> FR_POSTAL_CODE.matcher(upperPostalCode).matches();
            default -> true; // Accept any format for unsupported countries
        };
        
        if (!isValid) {
            errors.add(new ValidationError("postalCode", 
                "Invalid postal code format for country " + upperCountry));
        }
    }
    
    private static void validateRequiredField(String fieldName, String value, List<ValidationError> errors) {
        if (value == null || value.trim().isEmpty()) {
            errors.add(new ValidationError(fieldName, fieldName + " is required"));
        }
    }
}