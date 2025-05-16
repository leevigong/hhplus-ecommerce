package kr.hhplus.be.server.domain.sales;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ProductSalesCommand {

    @Getter
    public static class Popular {

        private final int top;
        private final LocalDate startDate;
        private final LocalDate endDate;

        private Popular(int top, LocalDate startDate, LocalDate endDate) {
            this.top = top;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        public static Popular of(int top, LocalDate startDate, LocalDate endDate) {
            return new Popular(top, startDate, endDate);
        }
    }
}
