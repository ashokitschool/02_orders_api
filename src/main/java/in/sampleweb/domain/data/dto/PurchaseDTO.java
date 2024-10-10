package in.sampleweb.domain.data.dto;

import lombok.Data;

import java.util.List;
@Data
public class PurchaseDTO {

    private CustomerDTO customer;
    private AddressDTO address;
    private OrderDTO order;
    private List<OrderItemDTO> orderItems;
}
