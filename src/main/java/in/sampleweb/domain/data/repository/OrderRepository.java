package in.sampleweb.domain.data.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import in.sampleweb.domain.data.dto.OrderDTO;
import in.sampleweb.domain.data.entity.Order;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    public Order findByRazorPayOrderId(String razorPayOrderId);
    public List<Order> findByEmail(String email);
    //public Order save(OrderDTO order);
}

