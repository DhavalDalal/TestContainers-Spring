package com.tsys.tc_spike;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
public class OncePerTestClassSpecs {
    // @Container: is used in conjunction with the @Testcontainers annotation
    // to mark containers that should be managed by the Testcontainers
    // extension.
    @Container
    private static final MySQLContainer MYSQL = new MySQLContainer("mysql:latest");
    // CREATING A MYSQL CONTAINER
    // using static final in the container instance, so the container
    // will be shared between all tests methods. The mysql container give
    // me some methods to configure a specific database name, username and
    // password. If we don't specify this, we will use default values
    // (database name: test, password: test, username: test)

    @Test
    public void containerHasStarted() {
        assertThat(MYSQL.isRunning(), is(true));
        assertThat(MYSQL.getJdbcUrl(), is(String.format("jdbc:mysql://localhost:%d/test", MYSQL.getFirstMappedPort())));
        assertThat(MYSQL.getUsername(), is("test"));
        assertThat(MYSQL.getPassword(), is("test"));
    }

    @Test
    public void useTheConnection() throws SQLException {
        final Connection connection = DriverManager.getConnection(MYSQL.getJdbcUrl(), MYSQL.getUsername(), MYSQL.getPassword());
        // use the connection from dataSource and run test as normal
        System.out.println("connection = " + connection);
        assertThat(connection, notNullValue());
    }
}