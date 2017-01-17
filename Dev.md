### Prepare integers ###
```bash
mvn -f ./integers/pom.xml clean com.almworks.integers:generator-maven-plugin:generate-java
mkdir integers/dev
cp -r integers/target/gen-java/* integers/dev/

mvn -f ./integers-wrappers/pom.xml clean com.almworks.integers:generator-maven-plugin:generate-java
mkdir integers-wrappers/dev
cp -r integers-wrappers/target/gen-java/* integers-wrappers/dev

# This step may be useful if you need several intermediate commits before Pull Request
git add integers/dev
git add integers-wrappers/dev
```
Open `Project Structure` dialog and manually mark as source roots:
 * `integers/dev` 
 * `integers-wrappers/dev`

### Development process ###
1. Add features and fix bugs in `integers/dev` and `integers-wrappers/dev` java sources until you're done
1. `mvn clean com.almworks.integers:generator-maven-plugin:generate-java`
1. Compare `dev` directories with the corresponding `target/gen-java` 
1. If there is difference - update templates in the corresponding `src\templates` and return to #2 
1. `mvn clean package` if build is successful - you are ready to create Pull Request
1. Don't forget to delete `dev` directories before your Pull Request is approved.  