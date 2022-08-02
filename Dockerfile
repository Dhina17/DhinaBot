#
# DhinaBot - A simple telegram bot
# Copyright (C) 2020-2022  Dhina17 <dhinalogu@gmail.com>
#
# SPDX-License-Identifier: GPL-3.0-or-later
#

# Stage - Build the maven project
FROM maven:3.8.6-openjdk-18-slim as buildstage

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



