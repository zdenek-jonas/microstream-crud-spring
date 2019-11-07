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
