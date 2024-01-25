package com.tsys.tc_spike;

import org.junit.jupiter.api.Test;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

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
@Testcontainers
public class MultipleContainersSpecs {
    // @Container: is used in conjunction with the @Testcontainers annotation
    // to mark containers that should be managed by the Testcontainers
    // extension.
    @Container
    private final MySQLContainer mysql = new MySQLContainer("mysql:latest")
            .withDatabaseName("test")
            .withUsername("test")
            .withPassword("test");
    // CREATING A MYSQL CONTAINER
    // using final in the container instance does not share the
    // so the container and will be started before each test and stopped
    // after each test.
    // The mysql container gives some methods to configure a specific
    // database name, username and password. If we don't specify this,
    // we will use default values
    // (database name: test, password: test, username: test)

    @Container
    private final KafkaContainer kafka = new KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"));

    @Test
    public void allContainersHaveStarted() {
        assertThat(mysql.isRunning(), is(true));
        assertThat(mysql.getJdbcUrl(), is(String.format("jdbc:mysql://localhost:%d/test", mysql.getFirstMappedPort())));
        assertThat(mysql.getUsername(), is("test"));
        assertThat(mysql.getPassword(), is("test"));

        assertThat(kafka.isRunning(), is(true));
        assertThat(kafka.getBootstrapServers(), is(String.format("PLAINTEXT://localhost:%d", kafka.getFirstMappedPort())));
    }

    @Test
    public void useTheConnection() throws SQLException {
        assertThat(mysql.getJdbcUrl(), notNullValue());
        final Connection connection = DriverManager.getConnection(mysql.getJdbcUrl(), mysql.getUsername(), mysql.getPassword());
        // use the connection from dataSource and run test as normal
        System.out.println("connection = " + connection);
        assertThat(connection, notNullValue());
    }

}