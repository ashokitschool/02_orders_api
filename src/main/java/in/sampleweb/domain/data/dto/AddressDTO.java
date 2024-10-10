package in.sampleweb.domain.data.dto;

import in.sampleweb.domain.data.entity.Customer;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class AddressDTO {

    private String houseno;
    private String street;
    private String city;
    private String state;
    private String zipCode;
    private String country;
    @ManyToOne
    private Customer customer;
}
