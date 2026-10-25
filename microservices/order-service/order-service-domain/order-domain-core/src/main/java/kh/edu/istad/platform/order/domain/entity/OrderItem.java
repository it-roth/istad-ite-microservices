package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.OrderId;
import kh.edu.istad.platform.order.domain.valueobject.OrderItemId;

public class OrderItem extends BaseEntity<OrderItemId> {

    private OrderId orderId;
    private final Product product;
    private final Integer quantity;
    private final Money price;
    private final Money subTotal;

    public void initializeOrderItem(OrderId orderId, OrderItemId orderItemId) {
        this.orderId = orderId;
        super.setId(orderItemId);
    }

    public boolean isPriceValid() {
        return product != null
                && product.getPrice() != null
                && product.getPrice().amount() != null
                && price != null
                && price.amount() != null
                && subTotal != null
                && subTotal.amount() != null
                && quantity != null
                && quantity > 0
                && price.isGreaterThanZero()
                && price.amount().compareTo(product.getPrice().amount()) == 0
                && price.multiply(quantity).amount().compareTo(subTotal.amount()) == 0;
    }

    // Getters
    public OrderId getOrderId() {
        return orderId;
    }

    public Product getProduct() {
        return product;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public Money getPrice() {
        return price;
    }

    public Money getSubTotal() {
        return subTotal;
    }

    // Builder
    private OrderItem(Builder builder) {
        super.setId(builder.orderItemId);
        this.orderId = builder.orderId;
        this.product = builder.product;
        this.quantity = builder.quantity;
        this.price = builder.price;
        this.subTotal = builder.subTotal;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderItemId orderItemId;
        private OrderId orderId;
        private Product product;
        private Integer quantity;
        private Money price;
        private Money subTotal;

        public Builder orderItemId(OrderItemId orderItemId) {
            this.orderItemId = orderItemId;
            return this;
        }

        public Builder orderId(OrderId orderId) {
            this.orderId = orderId;
            return this;
        }

        public Builder product(Product product) {
            this.product = product;
            return this;
        }

        public Builder quantity(Integer quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder subTotal(Money subTotal) {
            this.subTotal = subTotal;
            return this;
        }

        public OrderItem build() {
            return new OrderItem(this);
        }
    }
}
