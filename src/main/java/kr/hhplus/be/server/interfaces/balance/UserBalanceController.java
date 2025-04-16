package kr.hhplus.be.server.interfaces.balance;


import jakarta.validation.Valid;
import kr.hhplus.be.server.domain.balance.UserBalanceCommand;
import kr.hhplus.be.server.domain.balance.UserBalanceHistoryInfo;
import kr.hhplus.be.server.domain.balance.UserBalanceInfo;
import kr.hhplus.be.server.domain.balance.UserBalanceService;
import kr.hhplus.be.server.interfaces.balance.dto.ChargeRequest;
import kr.hhplus.be.server.interfaces.balance.dto.UserBalanceHistoryResponse;
import kr.hhplus.be.server.interfaces.balance.dto.UserBalanceResponse;
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
        UserBalanceInfo info = userBalanceService.getUserBalance(userId);

        return ResponseEntity.ok(UserBalanceResponse.UserBalanceV1.from(info));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserBalanceResponse.UserBalanceV1> chargeBalance(
            @PathVariable("userId") Long userId,
            @RequestBody @Valid ChargeRequest request
    ) {
        UserBalanceCommand.Charge command = UserBalanceCommand.Charge.of(userId, request.amount());
        UserBalanceInfo info = userBalanceService.charge(command);

        return ResponseEntity.ok(UserBalanceResponse.UserBalanceV1.from(info));
    }

    @GetMapping("/{userId}/history")
    public ResponseEntity<List<UserBalanceHistoryResponse.UserBalanceHistoryV1>> getUserBalanceHistory(
            @PathVariable("userId") Long userId
    ) {
        List<UserBalanceHistoryInfo> infos = userBalanceService.getUserBalanceHistory(userId);

        return ResponseEntity.ok(infos.stream()
                .map(UserBalanceHistoryResponse.UserBalanceHistoryV1::from)
                .toList());
    }
}
