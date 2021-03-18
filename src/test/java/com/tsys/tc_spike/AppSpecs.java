package com.tsys.tc_spike;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import javax.sql.DataSource;
import java.sql.SQLException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@ActiveProfiles("development")
// The TestPropertySource allows to override the spring.datasource.url property specified
// in the application-development.properties for which the setting is file database.  I want
// to run the app in dev mode using H2 file database, whereas for running tests frequently
// I want to use H2 in-memory database.
@TestPropertySource(properties = {
        // use memory instead of file.
        // URL Format: "jdbc:h2:{ {.|mem:}[name] | [file:]fileName | {tcp|ssl}:[//]server[:port][,server2[:port]]/name }[;key=value...]"
        "spring.datasource.url = jdbc:h2:mem:tcspikedb;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS TCSPIKE;"
//    "spring.datasource.url = jdbc:h2:mem:TCSPIKE;DB_CLOSE_DELAY=-1;INIT=CREATE SCHEMA IF NOT EXISTS TCSPIKE;"
})
@Tag("End-To-End-Test")
public class AppSpecs {

    @Autowired
    private DataSource dataSource;

    @Autowired
    private ApplicationContext context;

    @Test
    void contextLoads() throws SQLException {
        assertNotNull(context);
        assertNotNull(dataSource);
        final String url = dataSource.getConnection().getMetaData().getURL();
        assertThat(url, is("jdbc:h2:mem:tcspikedb"));
    }
}
