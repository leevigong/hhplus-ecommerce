package kr.hhplus.be.server.inferfaces.balance;


import kr.hhplus.be.server.domain.balance.TransactionType;
import kr.hhplus.be.server.inferfaces.balance.dto.ChargeRequest;
import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceHistoryResponse;
import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/balances")
public class BalanceController implements BalanceControllerDocs {

    @GetMapping("/{userId}")
    public ResponseEntity<UserBalanceResponse> getBalance(
            @PathVariable("userId") Long userId
    ) {
        UserBalanceResponse response = new UserBalanceResponse(userId, "이다은", BigDecimal.valueOf(100000));

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserBalanceResponse> updateBalance(
            @PathVariable("userId") Long userId,
            @RequestBody ChargeRequest request
    ) {
        UserBalanceResponse response = new UserBalanceResponse(userId, "이다은", BigDecimal.valueOf(6000));

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<UserBalanceHistoryResponse>> getUserBalanceHistory(
            @PathVariable("userId") Long userId
    ) {
        UserBalanceHistoryResponse history1 = new UserBalanceHistoryResponse(1L, TransactionType.CHARGE, BigDecimal.valueOf(10000), BigDecimal.valueOf(0), BigDecimal.valueOf(10000), LocalDateTime.now().withNano(0));
        UserBalanceHistoryResponse history2 = new UserBalanceHistoryResponse(1L, TransactionType.PAYMENT, BigDecimal.valueOf(10000), BigDecimal.valueOf(10000), BigDecimal.valueOf(0), LocalDateTime.now().withNano(0));
        UserBalanceHistoryResponse history3 = new UserBalanceHistoryResponse(1L, TransactionType.CHARGE, BigDecimal.valueOf(20000), BigDecimal.valueOf(0), BigDecimal.valueOf(20), LocalDateTime.now().withNano(0));
        List<UserBalanceHistoryResponse> responses = List.of(history1, history2, history3);

        return ResponseEntity.ok(responses);
    }
}
