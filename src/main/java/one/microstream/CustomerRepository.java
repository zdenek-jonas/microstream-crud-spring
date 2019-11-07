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
