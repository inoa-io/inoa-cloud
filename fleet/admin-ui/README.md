# Run for development

```sh
mvn package -Pdev 
mvn compile && java -Dmicronaut.environments=dev -cp target/lib/*:target/classes io.inoa.fleet.ui.Application
```
