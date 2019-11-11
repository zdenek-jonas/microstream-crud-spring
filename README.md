# Microstream Crud Spring Example

This example shows how to use Microstream with Spring. 
The goal is to provide a simple example how to start with Spring and Microstream and not to show all Spring or Microstream possibilities.

This project tries to make everything as simple as possible. For this reason, there are no Interfaces in Repository. 

## pom.xml
For first step we need a new Maven project. The Microstream Repository must bee added into pom.xml. 

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 https://maven.apache.org/xsd/maven-4.0.0.xsd"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance">
    <modelVersion>4.0.0</modelVersion>

    <groupId>one.microstream</groupId>
    <artifactId>microstream-spring-crud</artifactId>
    <version>0.0.1</version>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.2.0.RELEASE</version>
    </parent>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
        <microstream.version>02.00.01-MS-GA-SNAPSHOT</microstream.version>
        <lombok.version>1.18.10</lombok.version>
    </properties>

    <dependencies>
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <version>${lombok.version}</version>
            <scope>provided</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter</artifactId>
        </dependency>
        <dependency>
            <groupId>one.microstream</groupId>
            <artifactId>storage.embedded</artifactId>
            <version>${microstream.version}</version>
        </dependency>
        <dependency>
            <groupId>one.microstream</groupId>
            <artifactId>storage.embedded.configuration</artifactId>
            <version>${microstream.version}</version>
        </dependency>
    </dependencies>

    <repositories>
        <repository>
            <id>microstream-maven-releases</id>
            <url>https://repo.microstream.one/repository/maven-public/</url>
        </repository>
    </repositories>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
        </plugins>
    </build>

</project>
```

## Java Code
This project used Lombok framework to make Data object short.
After create maven project we need to add three Java files:
Customer.java 
```java
package one.microstream;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    private String firstName;
    private String lastName;
    private Long customerNumber;
}
```

Customer Repository. To make our example shortly, we just ignore gut practice and do not write any interface. Just direct implement a repository object.

```java
package one.microstream;

import one.microstream.storage.types.EmbeddedStorage;
import one.microstream.storage.types.EmbeddedStorageManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CustomerRepository {

    private EmbeddedStorageManager storage;
    private List<Customer> customers = new ArrayList<>();

    public CustomerRepository(@Value("${microstream.store.location}") String location) {
        storage = EmbeddedStorage.start(customers, new File(location));
    }

    public void storeAll() {
        storage.store(this.customers);
    }

    public void add(Customer customer) {
        customers.add(customer);
        storeAll();
    }

    public List<Customer> findAll() {
        return customers;
    }

    public void deleteAll() {
        customers.clear();
        storeAll();
    }

    public List<Customer> findByFirstName(String firstName) {
        return customers.stream().filter(c -> c.getFirstName().equals(firstName)).collect(Collectors.toList());
    }
}
```
And last java file is the Spring application self.

```java
package one.microstream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;

import java.util.Date;
import java.util.function.Consumer;

@SpringBootApplication
public class Application {

    private Consumer<Customer> logAll = c -> LOG.info(c.toString());

    private static final Logger LOG = LoggerFactory.getLogger(Application.class);

    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(Application.class);
        ctx.close();
        System.exit(0);
    }

    @Bean
    public CommandLineRunner crudDemo(CustomerRepository repository) {
        return (args) -> {
            repository.add(new Customer("Thomas", "Wresler", findCustomerNumber()));
            repository.add(new Customer("Jim", "Joe", findCustomerNumber()));
            repository.add(new Customer("Kamil", "Limitsky", findCustomerNumber()));
            repository.add(new Customer("Karel", "Ludvig", findCustomerNumber()));

            LOG.info("Our customers:");
            repository.findAll().forEach(logAll);
            LOG.info(" ");

            LOG.info("Find some specific customer");
            repository.findByFirstName("Karel").forEach(logAll);
            LOG.info(" ");

            LOG.info("Update name of all Customer");
            repository.findAll().forEach(c -> c.setFirstName("Johan"));
            repository.storeAll();
            repository.findAll().forEach(logAll);
            LOG.info(" ");

            LOG.info("Delete customers:");
            repository.deleteAll();
            repository.findAll().forEach(logAll);
            LOG.info(" ");
        };
    }

    private Long findCustomerNumber() {
        return new Date().getTime();
    }
}
```

Now we need a one file more and this file is application.properties. In this file we define the path for the DB files. Where Microstream should save our data. This file is easy and short. 
```
microstream.store.location=C:\\store
```
