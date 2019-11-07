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
