package in.sampleweb.domain.data.dto;

import in.sampleweb.domain.data.entity.Order;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class OrderItemDTO {

    private String imageUrl;
    private double unitPrice;
    private int quantity;
    private String prodname;

}
