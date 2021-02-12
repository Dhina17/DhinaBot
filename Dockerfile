# Stage - Build the maven project
FROM maven:3.6.3-jdk-11-slim as buildstage

WORKDIR /tmp/bot

# Copy the source to work directory
COPY . .

# Build the jar
RUN mvn clean package assembly:single -f "/tmp/bot/pom.xml"

# Stage - Execute the compiled jar
FROM archlinux/archlinux:base as runstage

# Update the base and install openjdk JRE
RUN pacman -Syu --noconfirm; \
      pacman -S --noconfirm jre-openjdk

WORKDIR /app

# Copy the OAuth tokens and tdlib session file to work directory
COPY ./tokens /app/tokens/
COPY ./tdlib /app/tdlib/

# Copy the compiled jar from the buildstage to work directory
COPY --from=buildstage /tmp/bot/target/dhinabot-0.3-jar-with-dependencies.jar /app/

# Execute the jar
CMD [ "java", "-jar", "dhinabot-0.3-jar-with-dependencies.jar" ]



