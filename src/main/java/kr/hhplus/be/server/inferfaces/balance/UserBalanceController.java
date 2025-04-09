package kr.hhplus.be.server.inferfaces.balance;


import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.balance.UserBalanceCommand;
import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.inferfaces.balance.dto.ChargeRequest;
import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceHistoryResponse;
import kr.hhplus.be.server.inferfaces.balance.dto.UserBalanceResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/balances")
public class UserBalanceController implements UserBalanceControllerDocs {

    private final UserBalanceService userBalanceService;

    public UserBalanceController(UserBalanceService userBalanceService) {
        this.userBalanceService = userBalanceService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserBalanceResponse.UserBalanceV1> getBalance(
            @PathVariable("userId") Long userId
    ) {
        UserBalanceResponse.UserBalanceV1 response = userBalanceService.getUserBalance(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserBalanceResponse.UserBalanceV1> chargeBalance(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid ChargeRequest request
    ) {
        UserBalanceCommand.Charge command = UserBalanceCommand.Charge.of(userId, request.amount());
        UserBalanceResponse.UserBalanceV1 response = userBalanceService.charge(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<UserBalanceHistoryResponse.UserBalanceHistoryV1>> getUserBalanceHistory(
            @PathVariable("userId") Long userId
    ) {
        List<UserBalanceHistoryResponse.UserBalanceHistoryV1> responses = userBalanceService.getUserBalanceHistory(userId);

        return ResponseEntity.ok(responses);
    }
}
