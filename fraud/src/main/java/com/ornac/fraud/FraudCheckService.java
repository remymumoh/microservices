package com.ornac.fraud;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
public class FraudCheckService {

    private final FraudCheckHistoryRepository fraudCheckHistoryRepository;

    public boolean isFraudulentCustomer(Integer customerId){
       fraudCheckHistoryRepository.save(
               FraudCheckHistory.builder().customerid(customerId)
                       .isFraudster(false)
                       .createdAt(LocalDateTime.now())
                       .build()
       );
       return false;
    }
}
