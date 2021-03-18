package com.tsys.tc_spike;

import org.testcontainers.containers.GenericContainer;
import org.testcontainers.utility.DockerImageName;

import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

// Manual container lifecycle control
// While Testcontainers was originally built with JUnit 4 integration in mind,
// it is fully usable with other test frameworks, or with no framework at all.
//
// Manually starting/stopping containers
// =====================================
// Containers can be started and stopped in code using start() and stop() methods.
// Additionally, container classes implement AutoCloseable.
// This enables better assurance that the container will be stopped at the
// appropriate time.
//
// Start Docker and run the following commands:
// $> docker info
// $> # using the Dockerfile in test/resources/testcontainer-spike run:
// $> docker build -t dd-ubuntu . -f Dockerfile
// $> docker images
// REPOSITORY          TAG                 IMAGE ID            CREATED             SIZE
// dd-ubuntu             latest              4c4dbcbd7504        6 minutes ago       101MB
// ...
// ...
//
// $> docker run -it d-ubuntu
//
//
// Depending on the situation you are you can use netcat for a lot of things
// like file transfering , port scanning , port redirection , hard drive
// cloning, http headers spoofing, chatting with your friend in the computer lab
// and more. The sky is the limit! You don’t need many command options of this
// tool to use netcat in different ways and for different purposes, if you know
// how to operate as a server and as a client and have imagination you can do things
// that nobody thought or did before. Netcat operates in two modes , as client or as
// a server so you can use netcat to connect to somewhere or listen for an inbound connection.
// The -l option, is the option  that makes the difference, if it is used with nc then
// netcat will operate in listening mode. The -p option allows the user to specify
// the port on which the server should listen.

public class GenericTestContainerSpike {
    public static void main(String[] args) {
        // It starts a container with the specific docker image (here, I'm using a dd-ubuntu image).
        // Another container called Ryuk will also be started and it's main task is to
        // manage the startup and stop of the container.
        // $> docker images
        //
        // REPOSITORY            TAG                 IMAGE ID            CREATED             SIZE
        // dd-ubuntu             latest              c167effd5792        24 hours ago        101MB
        // ubuntu                latest              4dd97cefde62        11 days ago         72.9MB
        // testcontainers/ryuk   0.3.1               ee7515743e6f        2 months ago        12MB
        // ...
        // ...
        try (GenericContainer container = new GenericContainer(DockerImageName.parse("dd-ubuntu"))
                .withExposedPorts(80)
                .withEnv("MAGIC_NUMBER", "42")
                .withCommand("/bin/sh", "-c",
                        "while true; do echo \"$MAGIC_NUMBER\" | nc -l -p 80; done")) {

            System.out.println("STARTING CONTAINER = " + container);
            container.start();
            assertThat(container.isRunning(), is(true));
            // ... use the container
            System.out.println("container.getContainerName() = " + container.getContainerName());
            System.out.println("container.getContainerId() = " + container.getContainerId());
            System.out.println("container.getContainerIpAddress() = " + container.getContainerIpAddress());
            System.out.println("container.getCommandParts())= ");
            Stream.of(container.getCommandParts()).forEach(System.out::println);
            System.out.println("container.getHost() = " + container.getHost());
            System.out.println("container.getFirstMappedPort() = " + container.getFirstMappedPort());

//            new Process()
            
            // no need to call stop() afterwards as this is AutoCloseable


        }

    }
}
