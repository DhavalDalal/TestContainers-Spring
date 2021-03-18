package com.tsys.tc_spike;

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

import javax.persistence.EntityManager;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;


@DataJpaTest
//
// @Testcontainers: is a JUnit Jupiter extension to activate automatic startup
// and stop of containers used in a test case.
// The extension supports two modes:
// 1. Containers that are restarted for every test method.  Containers declared as
//    static fields will be shared between test methods. They will be started
//    only once before any test method is executed and stopped after the last
//    test method has executed.
// 2. Containers that are shared between all methods of a test class.  Containers
//    declared as instance fields will be started and stopped for every test
//    method.
//
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
public class OncePerTestClassWithSpringIntegrationSpecs {
    // @Container: is used in conjunction with the @Testcontainers annotation
    // to mark containers that should be managed by the Testcontainers
    // extension.
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:latest")
            .withDatabaseName("tcspike")
            .withUsername("tcspikeUser")
            .withPassword("TcspikePassword");

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
        registry.add("spring.datasource.url"     , () -> MYSQL.getJdbcUrl());
        registry.add("spring.datasource.username", () -> MYSQL.getUsername());
        registry.add("spring.datasource.password", () -> MYSQL.getPassword());
    }

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