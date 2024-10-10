package in.sampleweb.domain.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.sampleweb.domain.data.dto.CustomerDTO;
import in.sampleweb.domain.data.entity.Customer;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {

    public Customer findByEmail(String email);

   
}

