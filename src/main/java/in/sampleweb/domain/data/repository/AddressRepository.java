package in.sampleweb.domain.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.sampleweb.domain.data.dto.AddressDTO;
import in.sampleweb.domain.data.entity.Address;

@Repository
public interface AddressRepository extends JpaRepository<Address, Long> {

    Address save(AddressDTO address);
   
}
