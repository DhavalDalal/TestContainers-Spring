package com.tsys.tc_spike.repository;
//Read Committed – This isolation level guarantees that any data read is committed at the moment it is read. Thus it does not allows dirty read. The transaction holds a read or write lock on the current row, and thus prevent other transactions from reading, updating or deleting it
import com.tsys.tc_spike.domain.Money;
import com.tsys.tc_spike.domain.Transaction;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import jakarta.persistence.EntityManager;
import javax.sql.DataSource;
import jakarta.transaction.Transactional;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.Instant;
import java.util.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

// TESTING REPOSITORIES
// ====================
// To test Spring Data JPA repositories, or any other JPA-related components,
// Spring Boot provides the @DataJpaTest annotation. We can just add it to
// our unit test and it will set up a Spring application context.
//
// The created application context will not contain the whole context needed
// for our Spring Boot application, but instead only a "part" of it containing
// the components needed to initialize any JPA-related components like our
// Spring Data repository. But we can inject a DataSource, JdbcTemplate or
// EntityManager into our test class if we need them. And finally, we can
// inject any of the Spring Data repositories from our application.
//
// All of the above components will be automatically configured to point to a
// actual MySQL database, within the Test Container.
//
// It also does not take into account the @TestPropertySource defined in other
// tests (for example, the @TestPropertySource in PaymentApplicationSpecs is not
// honoured here).
//
// Further, by default the application context containing all these components,
// is shared between all test methods within all @DataJpaTest annotated test classes.
//
// The @DataJpaTest meta-annotation contains the @Transactional annotation.
// This ensures our test execution is wrapped with a transaction that gets
// rolled-back after the test. This happens for both successful test
// cases as well as failures.  This way, the database state stays clean
// between tests and the tests stay independent of each other.
//
// TEST CONTAINERS:
// ================
// Jupiter integration is provided by means of the @Testcontainers annotation.
//
// This Extension supports two modes:
// 1. Containers that are restarted for every test method
// 2. Containers that are shared between all methods of a test class
//
// The extension finds all fields that are annotated with @Container and
// calls their container lifecycle methods (methods on the Startable interface).
// Containers declared as static fields will be shared between test methods.
// They will be started only once before any test method is executed and
// stopped after the last test method has executed. Containers declared as
// instance fields will be started and stopped for every test method.
//
// Note: Using the extension with parallel test execution is unsupported and
// may have unintended side effects.

// Instead of RunWith for Junit4, we use ExtendWith for Junit5.
//@ExtendWith(SpringExtension.class)
@DataJpaTest  // It already has @ExtendWith(SpringExtension.class), so we need not need an explicit one.
// @Testcontainers: is a JUnit Jupiter extension to activate automatic startup
// and stop of containers used in a test case.
@Testcontainers
//
// As we are using the MySQL Database from TestContainers,
// we have to tell to spring test framework that it should not try to
// replace our database (By default, DataJpaTest will use H2 in-memory
// database and make a connection to that one).  We can do that by using the
// @AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE)
// annotation
//
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Tag("IntegrationTest")
//@Disabled
public class TransactionRepositorySpecsUsingMySQLTestContainer {
    // @Container: is used in conjunction with the @Testcontainers annotation
    // to mark containers that should be managed by the Testcontainers
    // extension.
    @Container
    private static final MySQLContainer MYSQL = (MySQLContainer) new MySQLContainer(DockerImageName.parse("mysql:8.0.23"))
            .withDatabaseName("tcspike")
            .withUsername("tcspikeUser")
            .withPassword("TcspikePassword")
//            .withConfigurationOverride("mysql/my.cnf");
//            .withUrlParam("sessionVariables", "transaction_isolation='READ-COMMITTED'")
//            .withUrlParam("rewriteBatchedStatements", "true")
            .withCommand("mysqld --transaction_isolation=READ-COMMITTED");

    // CREATING A MYSQL CONTAINER
    // using static final in the container instance, so the container
    // will be shared between all tests methods. The mysql container give
    // me some methods to configure a specific database name, username and
    // password. If we don't specify this, we will use default values
    // (database name: test, password: test, username: test)
    //
    // By default, Data
    // In the traditional solution, we needed to do:
    //
    //  static class DatabaseEnvInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    //
    //        @Override
    //        public void initialize(ConfigurableApplicationContext applicationContext) {
    //            TestPropertyValues.of(
    //              String.format("spring.datasource.url=%s", MYSQL.getJdbcUrl()),
    //              String.format("spring.datasource.username=%s", MYSQL.getUsername()),
    //              String.format("spring.datasource.password=%s", MYSQL.getPassword()),
    //            ).applyTo(applicationContext);
    //        }
    //    }
    //
    // Spring Framework 5.2.5 introduced the @DynamicPropertySource annotation
    // to facilitate adding properties with dynamic values. All we have to do
    // is to create a static method annotated with @DynamicPropertySource and
    // having just a single DynamicPropertyRegistry instance as the input
    @DynamicPropertySource
    static void registerDatabaseProperties(DynamicPropertyRegistry registry) {
        // We create the schema manually using the script in src/main/resources/set-and-cleanup-db/mysql/03_schema.sql
        // Hence we set spring.jpa.hibernate.ddl-auto to none
//        registry.add("spring.jpa.database-platform", () -> "org.hibernate.dialect.MySQL8Dialect");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "none");
        registry.add("spring.jpa.database", () -> "MYSQL");
        // Using a classpath init script
//        Simply add it to the connection string, when connecting to the mysql database.
//                ?sessionVariables=transaction_isolation='READ-COMMITTED'
        registry.add("spring.datasource.url"     ,
                () -> String.format("%s?TC_INITSCRIPT=file:src/main/resources/mysql/03_schema.sql",
//                () -> String.format("%s?TC_MY_CNF=mysql/my.cnf&TC_INITSCRIPT=file:src/main/resources/mysql/03_schema.sql",
                        MYSQL.getJdbcUrl().replace("jdbc:", "jdbc:tc:")));

        // Using a init function
//        registry.add("spring.datasource.url"     ,
//                () -> String.format("%s?TC_INITFUNCTION=com.tsys.tcspike.repository.TransactionRepositorySpecsUsingMySQLTestContainer::setupSchema",
//                        MYSQL.getJdbcUrl().replace("jdbc:", "jdbc:tc:")));

//        registry.add("spring.datasource.url"     , () -> MYSQL.getJdbcUrl());
        registry.add("spring.datasource.username", () -> MYSQL.getUsername());
        registry.add("spring.datasource.password", () -> MYSQL.getPassword());
    }

    // The init function must be a public static method which takes a
    // java.sql.Connection as its only parameter
    public static void setupSchema(Connection connection) throws SQLException {
        // e.g. run schema setup or Flyway/liquibase/etc DB migrations here...
        System.out.println("TransactionRepositorySpecsUsingMySQLTestContainer.setupSchema called!");
        // Flyway flyway = Flyway
        //        .configure()
        //        .dataSource(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword())
        //        .locations(flywayPath)
        //        .load();
        // flyway.migrate();
    }

    private final UUID successfulTxnId = UUID.nameUUIDFromBytes("PASSED-TXNID-1".getBytes());
    private final String successfulOrderId = "PASSED-ORDER-ID-1";
    private final UUID failedTxnId = UUID.nameUUIDFromBytes("FAILED-TXNID-2".getBytes());
    private final String failedOrderId = "FAILED-ORDER-ID-2";

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;
    @Autowired
    private TransactionRepository transactionRepository;

    private Instant now = Instant.now();
    private final Transaction succeeded = new Transaction(successfulTxnId, now, "accepted", successfulOrderId, new Money(Currency.getInstance("INR"), 2000.45));
    private final Transaction failed = new Transaction(failedTxnId, now, "failed", failedOrderId, new Money(Currency.getInstance("INR"), 99.99));

    // When working with @DataJpaTest and an embedded database we can achieve
    // this with Hibernate's ddl-auto feature set to create-drop. This ensures
    // to first create the database schema based on our Java entity definitions
    // and then drops it afterward.
    //
    // While this works and might feel convenient (we don't have to write any
    // SQL), we should rather stick to our hand-crafted scripts. If we don't,
    // there might be a difference between our database schema during the
    // test and production.
    @Test
    public void containerHasStarted() {
        assertThat(MYSQL.isRunning(), is(true));
        assertThat(MYSQL.getJdbcUrl(), is(String.format("jdbc:mysql://localhost:%d/tcspike", MYSQL.getFirstMappedPort())));
        assertThat(MYSQL.getUsername(), is("tcspikeUser"));
        assertThat(MYSQL.getPassword(), is("TcspikePassword"));
    }

    @Test
    public void dependenciesAreInjected() {
        assertThat(dataSource, notNullValue());
        assertThat(jdbcTemplate, notNullValue());
        assertThat(entityManager, notNullValue());
        assertThat(transactionRepository, notNullValue());
    }

    // Which tests to write?
    // =====================
    // 1. Ideally for all inferred queries, one can avoid writing tests. If we have
    //    only one test that tries to start up the Spring application context in our
    //    code base, we do not need to write an extra test for our inferred query.
    //    Just the above test is enough because, if all the dependencies can be
    //    injected, it means that the system started successfully.
    //    NOTE: But I've written here just to demo how a Repository Integration tests
    //    can be written.
    //
    // 2. For Custom JPQL Queries, for simple ones, one can avoid writing tests.
    //    In case of complicated custom queries which might include joins with
    //    other tables or return aggregate objects instead of an entity, it is a good
    //    idea to write the tests for those.
    //
    // 3. For Native Queries, neither Hibernate nor Spring Data validate them at startup.
    //    Since the query may contain database-specific SQL, there’s no way Spring Data or
    //    Hibernate can know what to check for.
    //
    //    So, native queries are prime candidates for integration tests. However, if they
    //    really use database-specific SQL, those tests might not work with the embedded
    //    in-memory database, so we would have to provide a real database in the background
    //    (for instance in a docker container that is set up on-demand in the continuous
    //    integration pipeline).
    //    Alternatively, resort to ANSI-compliant SQL so that it work across different
    //    databases.

    // Tests for Inferred Queries
    @Test
    public void startsWithEmptyRepository() {
        assertThat(transactionRepository.count(), is(0L));
    }

    @Test
    public void findsNoTransactionsInAnEmptyRepository() {
        assertThat(toList(transactionRepository.findAll()), hasSize(0));
    }

    @Test
    public void findingByIdInAnEmptyRepositoryYieldsNothing() {
        assertThat(transactionRepository.findById(successfulTxnId), is(Optional.empty()));
    }

    @Test
    public void noTransactionsExistInAnEmptyRepository() {
        assertThat(transactionRepository.existsById(successfulTxnId), is(false));
//    assertThrows(EntityNotFoundException.class, () -> {
//      // Calling assertThat with nullValue() causes HibernateProxy to evaluate and
//      // then throw an javax.persistence.EntityNotFoundException, else
//      // the proxy does not evaluate and no exception in thrown by the following line:
//      // transactionRepository.getOne(txnId);
//      // Wrapping in assertThat causes proxy to evaluate and then throw an Exception.
//      assertThat(transactionRepository.getOne(txnId), nullValue());
//    });
    }

    @Test
    public void savesATransaction() {
        // When
        transactionRepository.save(succeeded);
        // Then
        assertThat(toList(transactionRepository.findAll()), hasSize(1));
        assertThat(transactionRepository.findById(successfulTxnId).orElseThrow(), is(succeeded));
    }

    @Test
    public void deletesATransaction() {
        // Given
        transactionRepository.save(succeeded);

        assert transactionRepository.findById(successfulTxnId).orElseThrow().equals(succeeded);

        // When
        transactionRepository.delete(succeeded);

        // Then
        assertThat(transactionRepository.findById(successfulTxnId), is(Optional.empty()));
    }

    @Test
    public void deletesById() {
        // Given
        transactionRepository.save(succeeded);
        assert transactionRepository.findById(successfulTxnId).orElseThrow().equals(succeeded);

        // When
        transactionRepository.deleteById(successfulTxnId);

        // Then
//        assertThat(toList(transactionRepository.findAll()), hasSize(0));
        assertThat(transactionRepository.findById(successfulTxnId), is(Optional.empty()));
    }

    @Test
    public void doesNotShoutWhenDeletingByNonExistentId() {
        assert transactionRepository.count() == 0;
        transactionRepository.deleteById(successfulTxnId);
        assert transactionRepository.count() == 0;
    }

    @Test
    @Disabled
    public void findsAllById() {
        // Given
        transactionRepository.saveAndFlush(succeeded);
        transactionRepository.saveAndFlush(failed);

        // When
        final var allById = transactionRepository.findAllById(List.of(successfulTxnId, failedTxnId));

        // Then
        assertThat(allById, hasSize(2));
        assertThat(allById, contains(succeeded, failed));
    }

    @Test
    public void savesAll() {
        // When
        transactionRepository.saveAll(List.of(succeeded, failed));

        // Then
        assertThat(transactionRepository.findById(successfulTxnId).orElseThrow(), is(succeeded));
        assertThat(transactionRepository.findById(failedTxnId).orElseThrow(), is(failed));
    }

    @Test
    public void deletesAll() {
        // Given
        assertThat(toList(transactionRepository.saveAllAndFlush(List.of(succeeded, failed))), hasSize(2));

        // When
        transactionRepository.deleteAllInBatch(List.of(succeeded, failed));

        // Then
        assertThat(toList(transactionRepository.findAllById(List.of(successfulTxnId, failedTxnId))), hasSize(0));
    }

    // Tests for Custom JPQL Queries
    @Test
    public void findsTransactionByOrderId() {
        transactionRepository.saveAll(List.of(succeeded, failed));

        assertThat(transactionRepository.findByOrderId(successfulOrderId).orElseThrow(), is(succeeded));
    }

    @Test
    public void findingTransactionByOrderIdInAnEmptyRepositoryYieldsNothing() {
        assertThat(transactionRepository.findByOrderId(successfulOrderId), is(Optional.empty()));
    }

    @Test
    @Disabled
    public void findsTransactionByTransactionIdAndOrderId() {
        transactionRepository.saveAll(List.of(succeeded, failed));
        transactionRepository.flush();

        assertThat(transactionRepository.findByTransactionIdAndOrderId(successfulTxnId, successfulOrderId), is(Optional.of(succeeded)));
    }

    @Test
    public void findingTransactionByTransactionIdAndOrderIdInAnEmptyRepositoryYieldsNothing() {
        assertThat(transactionRepository.findByTransactionIdAndOrderId(successfulTxnId, successfulOrderId), is(Optional.empty()));
    }

    // Tests for Native SQL Queries
    @Test
    public void findsAllTransactionByOrderIds() {
        transactionRepository.saveAll(List.of(succeeded, failed));

        assertThat(transactionRepository.findAllByOrderIds(List.of(successfulOrderId, failedOrderId)), hasSize(2));
    }

    @Test
    public void findingTransactionsByOrderIdsInAnEmptyRepositoryYieldsNothing() {
        assertThat(transactionRepository.findAllByOrderIds(List.of(successfulOrderId, failedOrderId)), hasSize(0));
    }

    private <T> List<T> toList(Iterable<T> iterable) {
        var list = new ArrayList<T>();
        iterable.forEach(list::add);
        return list;
    }
}
