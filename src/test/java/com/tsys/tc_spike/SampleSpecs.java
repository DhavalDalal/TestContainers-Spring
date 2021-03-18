package com.tsys.tc_spike;

// Improve performance of Tests
// ============================
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
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.utility.DockerImageName;

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
abstract class SingletonContainerSupport {

    // using static final in the container instance, so the container will be shared between all tests methods
    static final MySQLContainer MYSQL = new MySQLContainer(DockerImageName.parse("mysql:latest"))
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");

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
}

public class SampleSpecs extends SingletonContainerSupport {
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
