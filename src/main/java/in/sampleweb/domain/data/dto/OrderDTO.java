package in.sampleweb.domain.data.dto;

import java.util.List;

import in.sampleweb.domain.data.entity.Address;
import in.sampleweb.domain.data.entity.Customer;
import in.sampleweb.domain.data.entity.OrderItem;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class OrderDTO {

    private int totalquantity;
    private double totalprice;
    private String razorPayOrderId;
    private String orderStatus;
    private String razorpayPaymentId;
    
    @ManyToOne
    private Customer customer;
    
    @ManyToOne
    private Address address;
    
    @ElementCollection
    private List<OrderItem> orderItems; 
}
