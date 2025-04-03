package kr.hhplus.be.server.inferfaces.order;

import kr.hhplus.be.server.inferfaces.order.dto.OrderCreateRequest;
import kr.hhplus.be.server.inferfaces.order.dto.OrderResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController implements OrderControllerDocs {

    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(
            @RequestBody OrderCreateRequest request
    ){
        OrderResponse response = new OrderResponse(1L, BigDecimal.valueOf(5000), LocalDateTime.now().withNano(0));

        return ResponseEntity.ok(response);
    }
}
