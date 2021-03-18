# Test Containers [https://www.testcontainers.org/](https://www.testcontainers.org/)

Testcontainers is a Java library that supports JUnit tests, providing lightweight, throwaway instances of common databases, Selenium web browsers, or anything else that can run in a Docker container.

Testcontainers' generic container support offers the most flexibility, and makes it easy to use virtually any container images as temporary test dependencies. For example, if you might use it to test interactions with:

* **Data access layer integration tests:** Using an in-memory database like H2 in Java have some negatives as the tests may depend on features that in-memory databases can't reproduce. A few scenarios like using use provider-dependent queries, where our database logic may work at the time of local development but not when using the production database can cause some tests that have passed locally to fail in production.  This affects our tests reliability because we won't cover 100% the same scenarios as in our real environment.  Testing on a real database is much more profitable.  Use a containerized instance of a MySQL, PostgreSQL or Oracle database to test your data access layer code for complete compatibility, but without requiring complex setup on developers' machines and being assured that the tests will always start with a known DB state. NoSQL databases - e.g. redis, elasticsearch, mongo or any other database type that can also be containerized.

* **Application integration tests:** for running your application in a short-lived test mode with dependencies, such as message queues or web servers/proxies - e.g. nginx, apache.  Also, you can use it for integrating with Log services - logstash, kibana.

* **UI/Acceptance tests:** use containerized web browsers, compatible with Selenium, for conducting automated UI tests. Each test can get a fresh instance of the browser, with no browser state, plugin variations or automated browser upgrades to worry about. And you get a video recording of each test session, or just each session where tests failed.

* **Much more!** Other services developed by your team/organization which are already dockerized.  Check out the various contributed modules or create your own custom container classes using `GenericContainer` as a base.