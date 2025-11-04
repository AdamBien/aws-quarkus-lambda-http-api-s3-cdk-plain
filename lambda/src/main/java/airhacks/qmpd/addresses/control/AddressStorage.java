package airhacks.qmpd.addresses.control;

import java.lang.System.Logger;
import java.util.List;
import java.util.Optional;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import airhacks.qmpd.addresses.entity.Address;
import airhacks.qmpd.addresses.entity.AddressException;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.json.Json;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.model.S3Exception;
import software.amazon.awssdk.services.s3.model.S3Object;

/**
 * S3 storage implementation for address management.
 * 
 * Provides CRUD operations for address records using static client access
 * with JSON serialization. Each address is stored as a JSON object with the
 * address ID as the S3 object key.
 */
@ApplicationScoped
public class AddressStorage {

    private static final Logger logger = System.getLogger(AddressStorage.class.getName());

    @ConfigProperty(name = "address.bucket.name", defaultValue = "-not-set-")
    String bucketName;


    String key(String id){
        return id + ".json";
    }

    String key(Address address){
        var key = address.id();
        return key(key);
    }

    /**
     * Stores a new address record in S3.
     * 
     * @param address the address to store
     * @return the stored address
     * @throws AddressException if storage operation fails
     */
    public Address store(Address address) {
        var key = key(address.id());
        try {
            var json = address.toJSON().toString();
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("application/json")
                    .build();

            S3Access.CLIENT.putObject(putObjectRequest, RequestBody.fromString(json));
            logger.log(Logger.Level.DEBUG, "Stored address with id: {0}", address.id());
            return address;
        } catch (S3Exception e) {
            logger.log(Logger.Level.ERROR, "Failed to store address with id: {0}. Reason: {1}", address.id(), e);
            throw new AddressException("Failed to store address: " + e.getMessage(), e);
        }
    }

    /**
     * Finds an address by its unique identifier.
     * 
     * @param id the address identifier
     * @return Optional containing the address if found, empty otherwise
     * @throws AddressException if retrieval operation fails
     */
    public Optional<Address> findById(String id) {
        var key = key(id);
        try {
            var getObjectRequest = GetObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            var response = S3Access.CLIENT.getObject(getObjectRequest);
            var jsonReader = Json.createReader(response);
            var jsonObject = jsonReader.readObject();
            var address = Address.fromJSON(jsonObject);

            logger.log(Logger.Level.DEBUG, "Found address with id: {0}", id);
            return Optional.of(address);
        } catch (NoSuchKeyException e) {
            logger.log(Logger.Level.DEBUG, "Address not found with id: {0}", id);
            return Optional.empty();
        } catch (S3Exception e) {
            logger.log(Logger.Level.ERROR, "Failed to find address with id: {0} Reason: {1}", id, e);
            throw new AddressException("Failed to retrieve address: " + e.getMessage(), e);
        }
    }

    /**
     * Retrieves all address records from S3.
     * 
     * @return list of all addresses
     * @throws AddressException if list operation fails
     */
    public List<Address> findAll() {
        try {
            var listObjectsRequest = ListObjectsV2Request.builder()
                    .bucket(bucketName)
                    .build();

            var response = S3Access.CLIENT.listObjectsV2(listObjectsRequest);

            var addresses = response.contents()
                    .stream()
                    .map(S3Object::key)
                    .map(this::findById)
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .toList();

            logger.log(Logger.Level.DEBUG, "Retrieved {0} addresses", addresses.size());
            return addresses;
        } catch (S3Exception e) {
            logger.log(Logger.Level.ERROR, "Failed to list addresses {0}", e);
            throw new AddressException("Failed to retrieve addresses: " + e.getMessage(), e);
        }
    }

    /**
     * Updates an existing address record.
     *
     * @param address the address with updated information
     * @return the updated address
     * @throws AddressException         if update operation fails
     */
    public Address update(Address address) {
        var key = key(address);
        try {
            var json = address.toJSON().toString();
            var putObjectRequest = PutObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .contentType("application/json")
                    .build();

            S3Access.CLIENT.putObject(putObjectRequest, RequestBody.fromString(json));
            logger.log(Logger.Level.DEBUG, "Updated address with id: {0}", address.id());
            return address;
        } catch (S3Exception e) {
            logger.log(Logger.Level.ERROR, "Failed to update address with id: {0}. Reason: {1}", address.id(), e);
            throw new AddressException("Failed to update address: " + e.getMessage(), e);
        }
    }

    /**
     * Removes an address record from S3.
     *
     * @param id the address identifier to remove
     * @throws AddressException         if deletion operation fails
     */
    public void remove(String id) {
        var key = key(id);
        try {
            var deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            S3Access.CLIENT.deleteObject(deleteObjectRequest);
            logger.log(Logger.Level.DEBUG, "Removed address with id: {0}", id);
        } catch (S3Exception e) {
            logger.log(Logger.Level.ERROR, "Failed to remove address with id: {0}. Reason: {1}", id, e);
            throw new AddressException("Failed to remove address: " + e.getMessage(), e);
        }
    }
}