package airhacks.qmpd.addresses.boundary;

import jakarta.json.JsonObject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

/**
 * MicroProfile REST Client for AddressesResource system testing.
 * 
 * Provides client interface for testing address management endpoints
 * following MicroProfile guidelines with plural resource naming.
 * Uses service_uri configKey per MicroProfile requirements.
 */
@Path("/addresses")
@RegisterRestClient(configKey = "base_uri")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public interface AddressesResourceClient {
    
    /**
     * Creates a new address record.
     * 
     * @param request the address creation request as JSON
     * @return Response with created address and 201 status
     */
    @POST
    Response createAddress(JsonObject request);
    
    /**
     * Retrieves a specific address by ID.
     * 
     * @param id the address identifier
     * @return Response with address data and 200 status
     */
    @GET
    @Path("/{id}")
    Response getAddress(@PathParam("id") String id);
        
    /**
     * Updates an existing address record.
     * 
     * @param id the address identifier
     * @param request the address update request as JSON
     * @return Response with updated address and 200 status
     */
    @PUT
    @Path("/{id}")
    Response updateAddress(@PathParam("id") String id, JsonObject request);
    
    /**
     * Deletes an address record.
     * 
     * @param id the address identifier
     * @return Response with 204 No Content status
     */
    @DELETE
    @Path("/{id}")
    Response deleteAddress(@PathParam("id") String id);
}