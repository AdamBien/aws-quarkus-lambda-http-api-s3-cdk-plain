package airhacks.qmpd.addresses.control;

import airhacks.qmpd.addresses.entity.Address;

import java.util.List;

/**
 * Result of a paginated query containing addresses and pagination metadata.
 *
 * The nextToken contains the last address ID for cursor-based pagination.
 */
public record PaginationResult(
    List<Address> addresses,
    String nextToken,
    boolean hasMore
) {}
