package kr.hhplus.be.server.inferfaces.balance;


import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.balance.UserBalanceCommand;
import kr.hhplus.be.server.domain.balance.UserBalanceHistoryInfo;
import kr.hhplus.be.server.domain.balance.UserBalanceInfo;
import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.inferfaces.balance.dto.ChargeRequest;
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
    public ResponseEntity<UserBalanceInfo> getBalance(
            @PathVariable("userId") Long userId
    ) {
        UserBalanceInfo response = userBalanceService.getUserBalance(userId);

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserBalanceInfo> chargeBalance(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid ChargeRequest request
    ) {
        UserBalanceCommand.Charge command = UserBalanceCommand.Charge.of(userId, request.amount());
        UserBalanceInfo response = userBalanceService.charge(command);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<UserBalanceHistoryInfo>> getUserBalanceHistory(
            @PathVariable("userId") Long userId
    ) {
        List<UserBalanceHistoryInfo> responses = userBalanceService.getUserBalanceHistory(userId);

        return ResponseEntity.ok(responses);
    }
}
