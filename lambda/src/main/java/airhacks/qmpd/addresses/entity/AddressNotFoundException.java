package airhacks.qmpd.addresses.entity;

import jakarta.json.Json;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Exception for missing address records.
 * 
 * Thrown when attempting to access, update, or delete
 * an address that does not exist in the system.
 * Extends NotFoundException with self-contained Response building per MicroProfile guidelines.
 */
public class AddressNotFoundException extends NotFoundException {
    
    private final String addressId;
    
    public AddressNotFoundException(String addressId) {
        super("Address not found with id: " + addressId, createNotFoundResponse(addressId));
        this.addressId = addressId;
    }
    
    public AddressNotFoundException(String addressId, String message) {
        super(message, createNotFoundResponse(addressId, message));
        this.addressId = addressId;
    }
    
    /**
     * Gets the ID of the address that was not found.
     * 
     * @return the address ID that caused this exception
     */
    public String getAddressId() {
        return addressId;
    }
    
    private static Response createNotFoundResponse(String addressId) {
        return createNotFoundResponse(addressId, "Address not found with id: " + addressId);
    }
    
    private static Response createNotFoundResponse(String addressId, String message) {
        var errorResponse = Json.createObjectBuilder()
            .add("error", "ADDRESS_NOT_FOUND")
            .add("message", message)
            .add("addressId", addressId)
            .add("timestamp", LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
            .build();
        
        return Response.status(Response.Status.NOT_FOUND)
            .entity(errorResponse)
            .build();
    }
}