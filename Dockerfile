# Stage - Build the maven project
FROM maven:3.6.3-jdk-11-slim as buildstage

WORKDIR /tmp/bot

# Copy the pom.xml to the work dir
COPY pom.xml .

# Download the dependencies
RUN mvn dependency:go-offline -B

# Copy the source to the work dir
COPY . .

# Build the jar
RUN mvn clean package assembly:single -f "/tmp/bot/pom.xml" -B -DskipTests

# Stage - Execute the compiled jar
FROM archlinux/archlinux:base as runstage

# WORKAROUND for glibc 2.33 and old Docker
# See https://github.com/actions/virtual-environments/issues/2658
# Thanks to https://github.com/lxqt/lxqt-panel/pull/1562
RUN patched_glibc=glibc-linux4-2.33-4-x86_64.pkg.tar.zst && \
    curl -LO "https://repo.archlinuxcn.org/x86_64/$patched_glibc" && \
    bsdtar -C / -xvf "$patched_glibc"

# Update the base and install openjdk JRE
RUN pacman -Syu --noconfirm; \
      pacman -S --noconfirm jre-openjdk

WORKDIR /app

# Copy the OAuth credentials.json, tokens and tdlib session file to work directory
COPY ./credentials.json /app/
COPY ./tokens /app/tokens/
COPY ./tdlib /app/tdlib/

# Copy the compiled jar from the buildstage to work directory
COPY --from=buildstage /tmp/bot/target/dhinabot-1.0-jar-with-dependencies.jar /app/

# Execute the jar
CMD [ "java", "-jar", "dhinabot-1.0-jar-with-dependencies.jar" ]



