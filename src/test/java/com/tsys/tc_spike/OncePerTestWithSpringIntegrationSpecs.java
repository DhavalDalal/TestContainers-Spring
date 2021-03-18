package com.tsys.tc_spike;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.jdbc.core.JdbcTemplate;
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
@Testcontainers
// As we are using the MySQL Database from TestContainers, we have to tell
// to spring test framework that it should not try to replace our database.
// We can do that by using:
// @AutoConfigureTestDatabase(replace=AutoConfigureTestDatabase.Replace.NONE annotation.
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class OncePerTestWithSpringIntegrationSpecs {
    // @Container: is used in conjunction with the @Testcontainers annotation
    // to mark containers that should be managed by the Testcontainers
    // extension.
    @Container
    private final MySQLContainer mysql = new MySQLContainer("mysql:latest")
            .withDatabaseName("tcspike")
            .withUsername("tcspikeUser")
            .withPassword("TcspikePassword");
    // CREATING A MYSQL CONTAINER
    // using final in the container instance does not share the
    // so the container and will be started before each test and stopped
    // after each test.
    // The mysql container gives some methods to configure a specific
    // database name, username and password. If we don't specify this,
    // we will use default values
    // (database name: test, password: test, username: test)

    @Test
    public void containerHasStarted() {
        assertThat(mysql.isRunning(), is(true));
        assertThat(mysql.getJdbcUrl(), notNullValue());
        assertThat(mysql.getUsername(), is("tcspikeUser"));
        assertThat(mysql.getPassword(), is("TcspikePassword"));
    }

    @Test
    public void useTheConnection() throws SQLException {
        final Connection connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        // use the connection from dataSource and run test as normal
        System.out.println("connection = " + connection);
        assertThat(connection, notNullValue());
    }

    @Autowired
    private DataSource dataSource;
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private EntityManager entityManager;

    @Test
    public void dependenciesAreInjected() {
        assertThat(mysql.isRunning(), is(true));
        assertThat(dataSource, notNullValue());
        System.out.println("dataSource = " + dataSource);
        // use the connection from dataSource and run test as normal
        assertThat(jdbcTemplate, notNullValue());
        System.out.println("jdbcTemplate = " + jdbcTemplate);

        assertThat(entityManager, notNullValue());
        System.out.println("entityManager = " + entityManager);
    }
}