# Maven Deployment Guide

This guide explains how to build and deploy the ML4J library.

## Prerequisites

- Java Development Kit (JDK) 17 or higher.
- Apache Maven installed and configured.

## Building Locally

To build the project and generate the JAR file, run:

```bash
mvn clean install
```

This command will:
1.  Compile the source code.
2.  Run any tests (if available).
3.  Package the compiled code into a JAR file located in the `target/` directory (e.g., `ml4j-1.0.0.jar`).
4.  Install the artifact into your local Maven repository (`~/.m2/repository`).

Once installed locally, you can use it in other projects on your machine by adding the dependency to their `pom.xml`.

## Deploying to a Remote Repository

To deploy to a remote repository (like Maven Central or a private Nexus/Artifactory server), you need to configure the `<distributionManagement>` section in your `pom.xml` and authentication in your `~/.m2/settings.xml`.

### 1. Configure `pom.xml`

Add the following to your `pom.xml` (replace URLs with your repository URLs):

```xml
<distributionManagement>
    <repository>
        <id>central</id>
        <name>Central Repository</name>
        <url>https://oss.sonatype.org/service/local/staging/deploy/maven2/</url>
    </repository>
    <snapshotRepository>
        <id>snapshots</id>
        <name>Snapshot Repository</name>
        <url>https://oss.sonatype.org/content/repositories/snapshots/</url>
    </snapshotRepository>
</distributionManagement>
```

### 2. Configure `settings.xml`

Add your credentials to `~/.m2/settings.xml`:

```xml
<servers>
    <server>
        <id>central</id>
        <username>your-username</username>
        <password>your-password</password>
    </server>
</servers>
```

### 3. Deploy

Run the deploy command:

```bash
mvn deploy
```

## Releasing to Maven Central

Releasing to Maven Central requires additional steps, including:
-   GPG Signing of artifacts.
-   Javadoc and Source attachment (already configured in `pom.xml`).
-   Registering a namespace with Sonatype OSSRH.

For a detailed guide on publishing to Maven Central, refer to the [official documentation](https://central.sonatype.org/publish/publish-guide/).
