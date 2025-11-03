package airhacks.qmpd.addresses.boundary;

import airhacks.qmpd.addresses.control.AddressStorage;
import airhacks.qmpd.addresses.control.AddressValidator;
import airhacks.qmpd.addresses.entity.Address;
import airhacks.qmpd.addresses.entity.AddressNotFoundException;
import airhacks.qmpd.addresses.entity.AddressResponse;
import airhacks.qmpd.addresses.entity.AddressUpdateRequest;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.json.JsonObject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * JAX-RS resource for address management operations.
 * 
 * Provides RESTful endpoints for CRUD operations on address records.
 * Follows MicroProfile guidelines with plural resource naming.
 */
@Path("/addresses")
@ApplicationScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class AddressesResource {

    static final System.Logger LOGGER = System.getLogger(AddressesResource.class.getName());

    @Inject
    AddressStorage storage;
    
    /**
     * Creates a new address record.
     *
     * @param json the address creation request
     * @return Response with created address and 201 status
     */
    @POST
    public Response createAddress(JsonObject json) {
        LOGGER.log(System.Logger.Level.INFO, "Creating address from request: {0}", json);
        var address = Address.fromCreateRequest(json);
        AddressValidator.validateForCreation(address);
        var stored = storage.store(address);
        LOGGER.log(System.Logger.Level.INFO, "Address created with ID: {0}", stored.id());
        var response = AddressResponse.from(stored);

        return Response.status(Response.Status.CREATED)
            .entity(response.toJSON())
            .build();
    }
    
    /**
     * Retrieves a specific address by ID.
     * 
     * @param id the address identifier
     * @return Response with address data and 200 status
     */
    @GET
    @Path("/{id}")
    public Response getAddress(@PathParam("id") String id) {
        var addressOpt = storage.findById(id);
        if (addressOpt.isEmpty()) {
            throw new AddressNotFoundException(id);
        }
        
        var address = addressOpt.get();
        var response = AddressResponse.from(address);
        
        return Response.ok(response.toJSON()).build();
    }
    
    
    /**
     * Updates an existing address record.
     * 
     * @param id the address identifier
     * @param request the address update request
     * @return Response with updated address and 200 status
     */
    @PUT
    @Path("/{id}")
    public Response updateAddress(@PathParam("id") String id, JsonObject json) {
        var request = AddressUpdateRequest.fromJSON(json);
        // Verify address exists
        var existingAddressOpt = storage.findById(id);
        if (existingAddressOpt.isEmpty()) {
            throw new AddressNotFoundException(id);
        }
        
        var existingAddress = existingAddressOpt.get();
        AddressValidator.validateForUpdate(request);
        var updatedAddress = existingAddress.update(request);
        var stored = storage.update(updatedAddress);
        var response = AddressResponse.from(stored);
        
        return Response.ok(response.toJSON()).build();
    }
    
    /**
     * Deletes an address record.
     * 
     * @param id the address identifier
     * @return Response with 204 No Content status
     */
    @DELETE
    @Path("/{id}")
    public Response deleteAddress(@PathParam("id") String id) {
        storage.remove(id);
        
        return Response.noContent().build();
    }
}