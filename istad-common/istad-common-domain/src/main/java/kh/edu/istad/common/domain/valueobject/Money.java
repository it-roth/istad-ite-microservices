package kh.edu.istad.common.domain.valueobject;

import java.math.BigDecimal;

// 150$ => new Money(150)
// 100$ => new Money(100)
public record Money(
        BigDecimal amount
) {
    public void isGreaterThanZero() {
        if (!(amount.compareTo(BigDecimal.ZERO) > 0)){
            System.out.println("Money is not greater than zero");
            throw new RuntimeException("Money is not greater than zero");
        }
    }
    // More logic
}
