# Deploying of new release version

To release a new version, you need to manually publish it to Maven Central via the Sonatype Publisher Portal. This requires the following steps:

1. Set up and configure GPG key for generating a signature. See this [guide](https://central.sonatype.org/publish/requirements/gpg/) 
for more information.
   1. Install GnuPG or jump to the next point. 
   2. Using `gpg --list-signatures` check if you have a key. If not, generate one using `gpg --gen-key`.
   3. During the creation process, you will need to create a passphrase. This passphrase and your private key are all that
   is needed to sign artifacts with your signature.
   4. Remember to add your public key to the [key server](https://central.sonatype.org/publish/requirements/gpg/#distributing-your-public-key) 
      so that others can verify your signature.
   5. Set environment variable `GPG_TTY`:
      ```
      export GPG_TTY=$(tty)
      ```
2. Configure account details for the Publisher Portal
   1. Find the configuration file for Maven - `settings.xml` file. It is located either in `~/.m2/settings.xml` 
   (single user configuration) or in `/opt/homebrew/Cellar/maven/[version]/libexec/conf` (configuration for all Maven users on a machine).
   2. [Generate a user token](https://central.sonatype.org/publish/generate-portal-token/) in the Sonatype Publisher Portal. The credentials to log in are in 1Password – look for "Sonatype Stargate". Add the token data to `settings.xml`:
    ```xml
    <settings>
      <servers>
        <server>
          <id>central</id>
          <username>[portal token username]</username>
          <password>[portal token password]</password>
        </server>
      </servers>
    </settings>
    ```
   3. Add GPG passphrase to the `settings.xml` (optional, if not using gpg-agent):
   ```xml
   <settings>
     <profiles>
       <profile>
         <id>central</id>
         <properties>
           <gpg.passphrase>[passphrase]</gpg.passphrase>
         </properties>
       </profile>
     </profiles>
   </settings>
   ```
3. And finally publish the new version:
```
mvn clean deploy
```

4. Check that a new version is available in [Maven central](https://central.sonatype.com/artifact/io.github.productboardlabs/jackson-kafka-avro-serializer/overview) 
(publishing may take a few minutes).


Follow this [guide](https://central.sonatype.org/publish/publish-portal-maven/) for more information.

