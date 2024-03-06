package com.tsys.tc_spike;

// 4. Improve performance of Tests with Spring Integration
// =======================================================
// We always try to make the test feedback loop very short and
// make our tests run faster. It is not so complex to improve
// the performance of our tests, just follow the below 2 points:
//
//  1. Remove the @TestContainers and @Container annotation from our
//     test, this will force Junit 5 to re-start the container on every
//     test and it will not allow us to re-use them.
//  2. Follow the singleton container approach as mentioned on the
//     https://www.testcontainers.org/test_framework_integration/manual_lifecycle_control/#singleton-containers

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

import jakarta.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

// Singleton Containers
// The singleton container is started only once when the base class is loaded.
// The container can then be used by all inheriting test classes.
// At the end of the test suite the Ryuk container that is started by Testcontainers
// core will take care of stopping the singleton container.
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
abstract class SingletonContainerSupportWithSpringIntegration {

    // using static final in the container instance, so the container will be shared between all tests methods
    static final MySQLContainer MYSQL = new MySQLContainer(DockerImageName.parse("mysql:latest"))
            .withDatabaseName("tcspike")
            .withUsername("tcspikeUser")
            .withPassword("TcspikePassword");

    static {
        // this makes sure that the Container is started only once.
        MYSQL.start();
        System.out.println("MYSQL.getDockerImageName() = " + MYSQL.getDockerImageName());
        System.out.println("MYSQL.getDriverClassName() = " + MYSQL.getDriverClassName());
        System.out.println("MYSQL.getContainerName() = " + MYSQL.getContainerName());
        System.out.println("MYSQL.getContainerId() = " + MYSQL.getContainerId());
        System.out.println("MYSQL.getContainerIpAddress() = " + MYSQL.getContainerIpAddress());
        System.out.println("MYSQL.getExposedPorts() = " + MYSQL.getExposedPorts());
        System.out.println("MYSQL.getFirstMappedPort() = " + MYSQL.getFirstMappedPort());
        System.out.println("MYSQL.getWorkingDirectory() = " + MYSQL.getWorkingDirectory());

        System.out.println("MYSQL.getJdbcUrl() = " + MYSQL.getJdbcUrl());
        System.out.println("MYSQL.getDatabaseName() = " + MYSQL.getDatabaseName());
        System.out.println("MYSQL.getPassword() = " + MYSQL.getPassword());
    }

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
        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl());
        registry.add("spring.datasource.username", () -> MYSQL.getUsername());
        registry.add("spring.datasource.password", () -> MYSQL.getPassword());
    }
}

public class SingletonContainerForAllSpringSpecs extends SingletonContainerSupportWithSpringIntegration {

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void containerHasStarted() {
        assertThat(MYSQL.isRunning(), is(true));
        assertThat(MYSQL.getJdbcUrl(), is(String.format("jdbc:mysql://localhost:%d/tcspike", MYSQL.getFirstMappedPort())));
        assertThat(MYSQL.getUsername(), is("tcspikeUser"));
        assertThat(MYSQL.getPassword(), is("TcspikePassword"));
    }

    @Test
    public void useTheConnection() throws SQLException {
        final Connection connection = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
        // use the connection from dataSource and run test as normal
        System.out.println("connection = " + connection);
        assertThat(connection, notNullValue());
    }

    @Test
    public void dependenciesAreInjected() {
        assertThat(dataSource, notNullValue());
        System.out.println("dataSource = " + dataSource);
        // use the connection from dataSource and run test as normal
        assertThat(jdbcTemplate, notNullValue());
        System.out.println("jdbcTemplate = " + jdbcTemplate);

        assertThat(entityManager, notNullValue());
        System.out.println("entityManager = " + entityManager);
    }
}
