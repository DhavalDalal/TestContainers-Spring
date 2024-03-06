package com.tsys.tc_spike.repository;

import com.tsys.tc_spike.domain.Transaction;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends CrudRepository<Transaction, UUID> {
//public interface TransactionRepository extends PagingAndSortingRepository<Transaction, String> {
//public interface TransactionRepository extends JpaRepository<Transaction, String> {

    // We have 3 different options to create queries.
    // 1. Create an INFERRED QUERY:
    //
    // Optional<User> findByName(String name);
    // We don’t need to tell Spring Data what to do, since it automatically infers the
    // SQL query from the name of the method name.
    //
    // It is better to return Optional<User> instead of User
    Optional<Transaction> findByOrderId(String orderId);

    // What’s nice about this feature is that Spring Data also automatically checks
    // if the query is valid at startup. If we renamed the method to findByFoo() and
    // the User does not have a property foo, Spring Data will point that out to us
    // with an exception:
    //
    // org.springframework.data.mapping.PropertyReferenceException:
    // No property foo found for type User!
    //
    // Uncomment the line below and see the error for yourself
    //  Transaction findByFoo();

    // So, as long as we have at least one test that tries to start up the Spring
    // application context in our code base, we do not need to write an extra test
    // for our inferred query.
    //
    // Note that this is not true for queries inferred from long method names
    // like findByNameAndRegistrationDateBeforeAndEmailIsNotNull(). This method name
    // is hard to grasp and easy to get wrong, so we should test if it really does
    // what we intended.
    //
    //  Having said this, it’s good practice to rename such methods to a shorter,
    //  more meaningful name and add a @Query annotation to provide a custom JPQL query.
    //
    // 2. Custom JPQL Queries with @Query
    // ==================================
    // If queries become more complex, it makes sense to provide a custom JPQL query:
    @Query("select t from Transaction t where t.id = :transactionId and t.orderId = :orderId")
//  Optional<Transaction> findByTransactionIdAndOrderId(@Param("transactionId") String transactionId,
//                                                      @Param("orderId") String orderId);
    Optional<Transaction> findByTransactionIdAndOrderId(@Param("transactionId") UUID transactionId,
                                                        @Param("orderId") String orderId);


    // Similar to inferred queries, we get a validity check for those JPQL queries for free.
    // Using Hibernate as our JPA provider, we’ll get a QuerySyntaxException on startup
    // if it found an invalid query:
    //
    //  java.lang.IllegalArgumentException: org.hibernate.QueryException:
    //  could not resolve property: foo of: com.tsys.tcspike.domain.Transaction [select t from com.tsys.tcspike.domain.Transaction t where t.id = :transactionId and t.foo = :orderId]org.hibernate.hql.internal.ast.QuerySyntaxException:
    //
    // Uncomment the lines below to see that error:
    //  @Query("select t from Transaction t where t.id = :transactionId and t.foo = :orderId")
    //  Optional<Transaction> findByTransactionIdAndOrderId(@Param("transactionId") String transactionId,
    //                                                      @Param("orderId") String orderId);
    //

    // Custom queries, however, can get a lot more complicated than finding an entry
    // by a single attribute. They might include joins with other tables or return
    // complex DTOs instead of an entity.

    // So, should we write tests for custom queries?
    // The unsatisfying answer is that we have to decide for ourselves
    // if the query is complex enough to require a test.

    // 3. Native Queries with @Query
    // =============================
    // Another way is to use a native query:
    // Instead of specifying a JPQL query, which is an abstraction over SQL,
    // we’re specifying an SQL query directly. This query may use a database-specific
    // SQL dialect.  In order to be compliant across different databases, one in test
    // and another in production, it is important to write ANSI-compliant SQL.
    @Query(value = "SELECT t.* FROM transactions AS t WHERE t.order_id IN :orderIds", nativeQuery = true)
    List<Transaction> findAllByOrderIds(@Param("orderIds") Iterable<String> orderIds);

    // IMPORTANT NOTE: Neither Hibernate nor Spring Data validate native queries at startup.
    // Since the query may contain database-specific SQL, there’s no way Spring Data or
    // Hibernate can know what to check for.
    //
    // So, native queries are prime candidates for integration tests. However, if they
    // really use database-specific SQL, those tests might not work with the embedded
    // in-memory database, so we would have to provide a real database in the background
    // (for instance in a docker container that is set up on-demand in the continuous
    // integration pipeline).
    //
}
