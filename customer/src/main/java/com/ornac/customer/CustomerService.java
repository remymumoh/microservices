package com.ornac.customer;

import com.ornac.amqp.RabbitMQMessageProducer;
import com.ornac.clients.fraud.FraudCheckResponse;
import com.ornac.clients.fraud.FraudClient;
import com.ornac.clients.notification.NotificationClient;
import com.ornac.clients.notification.NotificationRequest;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@AllArgsConstructor
public class CustomerService {
    private final CustomerRepository customerRepository;
    private  final RestTemplate restTemplate;
    private final FraudClient fraudClient;
    private final RabbitMQMessageProducer rabbitMQMessageProducer;

    public void registerCustomer(CustomerRegistrationRequest customerRegistrationRequest) {
        Customer customer = Customer.builder().firstName(customerRegistrationRequest.firstName())
                .lastName(customerRegistrationRequest.lastName())
                .email(customerRegistrationRequest.email())
                .build();

        //todo : check if email is valid
        customerRepository.saveAndFlush(customer);
        //todo : check if email is not taken
        //todo: check if fraudster

        FraudCheckResponse fraudCheckResponse =
                fraudClient.isFraudster(customer.getId());


        if (fraudCheckResponse != null && fraudCheckResponse.isFraudSter()) {
            throw new IllegalArgumentException("it is a fraudster");
        }

        NotificationRequest notificationRequest = new NotificationRequest(customer.getId(),
                customer.getEmail(),
                String.format("Hi %s, Welcome to OrnacCodes...",
                        customer.getFirstName()));

       rabbitMQMessageProducer.publish(
               notificationRequest,
               "internal.exchange",
               "internal.notification.routing-key"
       );
    }
}
