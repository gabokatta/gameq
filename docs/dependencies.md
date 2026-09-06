# Dependency verification

After adding or updating a dependency, regenerate the verification metadata:

```sh
./gradlew --refresh-dependencies --write-verification-metadata sha256 build
```

Review the generated checksums:

```sh
git diff -- gradle/verification-metadata.xml
```

Then verify the build without updating its trust metadata:

```sh
./gradlew --refresh-dependencies build
```
