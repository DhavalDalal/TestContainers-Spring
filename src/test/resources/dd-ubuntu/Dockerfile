# $> docker build -t dd-ubuntu . -f Dockerfile

# Layer 1. Base Ubuntu Layer
FROM ubuntu
# Layer 2. update apt-get
RUN apt-get update
# Layer 3. Install netcat
RUN apt-get -y install netcat
# Layer 4. nc binds to port 80 so we use the EXPOSE instruction to
# have it mapped by the docker daemon
EXPOSE 80
# Layer 5. Run command nc and put this in server mode listening on port 80
CMD 'nc' '-l' '-p' '80'
