package in.sampleweb.domain.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.sampleweb.domain.data.dto.OrderItemDTO;
import in.sampleweb.domain.data.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    void save(OrderItemDTO item);
  
}
