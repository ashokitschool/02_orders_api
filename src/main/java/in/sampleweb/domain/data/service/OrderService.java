package in.sampleweb.domain.data.service;

import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import in.sampleweb.domain.data.dto.OrderItemDTO;
import in.sampleweb.domain.data.dto.OrderResponse;
import in.sampleweb.domain.data.dto.PaymentCallbackDTO;
import in.sampleweb.domain.data.dto.PurchaseDTO;
import in.sampleweb.domain.data.entity.Customer;
import in.sampleweb.domain.data.entity.Order;
import in.sampleweb.domain.data.entity.OrderItem;
import in.sampleweb.domain.data.entity.Address;
import in.sampleweb.domain.data.repository.AddressRepository;
import in.sampleweb.domain.data.repository.CustomerRepository;
import in.sampleweb.domain.data.repository.OrderItemRepository;
import in.sampleweb.domain.data.repository.OrderRepository;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepo;

    @Autowired
    private CustomerRepository custRepo;

    @Autowired
    private AddressRepository addressRepo;

    @Autowired
    private OrderItemRepository orderItemRepo;

    private RazorpayClient client;

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    public OrderResponse createOrder(PurchaseDTO purchaseDto) throws Exception {
        // Create a Razorpay order
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", purchaseDto.getOrder().getTotalprice() * 100);  // amount in paise
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", purchaseDto.getCustomer().getEmail());

        // Initialize Razorpay client
        this.client = new RazorpayClient(keyId, keySecret);
        com.razorpay.Order razorPayOrder = client.Orders.create(orderRequest);

        Customer customer = custRepo.findByEmail(purchaseDto.getCustomer().getEmail());
        if(customer == null){
            customer = new Customer();
            customer.setName(purchaseDto.getCustomer().getName());
            customer.setEmail(purchaseDto.getCustomer().getEmail());
            customer.setPhno(purchaseDto.getCustomer().getPhno()); // Assuming there's a phone number field
            custRepo.save(customer);
        }

        // Save address information
        Address address = new Address();
        address.setCustomer(customer); // Link address to the customer
        address.setStreet(purchaseDto.getAddress().getStreet());
        address.setCity(purchaseDto.getAddress().getCity());
        address.setState(purchaseDto.getAddress().getState());
        address.setZipCode(purchaseDto.getAddress().getZipCode());
        addressRepo.save(address);

        // Save order information
        Order newOrder = new Order();
        String orderTrackingNum = generateOrderTrackingId();
        newOrder.setOrderTrackingNum(orderTrackingNum);
        newOrder.setRazorPayOrderId(razorPayOrder.get("id"));
        newOrder.setOrderStatus(razorPayOrder.get("status"));
        newOrder.setTotalPrice(purchaseDto.getOrder().getTotalprice());
        newOrder.setEmail(customer.getEmail());
        newOrder.setCustomer(customer);
        newOrder.setAddress(address); // Link order to the address
        orderRepo.save(newOrder);

        // Save order items
        List<OrderItemDTO> orderItems = purchaseDto.getOrderItems(); // Assuming this returns a list of items
        for (OrderItemDTO itemDTO : orderItems) {
            OrderItem orderItem = new OrderItem();
            BeanUtils.copyProperties(itemDTO, orderItem);
            orderItem.setOrder(newOrder); // Link each item to the order
            orderItemRepo.save(orderItem);
        }

        // Create and return the OrderResponse
        OrderResponse orderResponse = new OrderResponse();
        orderResponse.setRazorpayOrderId(razorPayOrder.get("id"));
        orderResponse.setOrderStatus(razorPayOrder.get("status"));
        orderResponse.setOrderTrackingNumber(orderTrackingNum);

        return orderResponse;
    }

    public Order verifyPaymentAndUpdateOrderStatus(PaymentCallbackDTO paymentCallbackDTO) {
        Order order = orderRepo.findByRazorPayOrderId(paymentCallbackDTO.getRazorpayOrderId());
        if (order != null) {
            try {
                // Verify the payment signature
                boolean isValid = verifySignature(paymentCallbackDTO);

                if (isValid) {
                    // Update order status and save the order
                    order.setOrderStatus("PAID");
                    order.setRazorPayPaymentId(paymentCallbackDTO.getRazorpayPaymentId());
                    orderRepo.save(order);  // Save updated order to the database
                    return order;
                } else {
                    System.out.println("Invalid payment signature.");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    private boolean verifySignature(PaymentCallbackDTO paymentCallbackDTO) throws RazorpayException {
        String generatedSignature = HmacSHA256(
                paymentCallbackDTO.getRazorpayOrderId() + "|" + paymentCallbackDTO.getRazorpayPaymentId(),
                keySecret
        );
        return generatedSignature.equals(paymentCallbackDTO.getRazorpaySignature());
    }

    private String HmacSHA256(String data, String key) throws RazorpayException {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(), "HmacSHA256");
            mac.init(secretKeySpec);
            byte[] hash = mac.doFinal(data.getBytes());
            return new String(Hex.encodeHex(hash));
        } catch (Exception e) {
            throw new RazorpayException("Failed to calculate signature.", e);
        }
    }

    public String generateOrderTrackingId() {
        // Get the current timestamp
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        String timestamp = sdf.format(new Date());

        // Generate a random UUID for uniqueness
        String randomUUID = UUID.randomUUID().toString().substring(0, 5).toUpperCase();

        // Combine timestamp and UUID to form the tracking ID
        return "OD_" + timestamp + "_" + randomUUID;
    }
}
