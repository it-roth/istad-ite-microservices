package kh.edu.istad.platform.business.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;

import java.util.UUID;

public class Product extends BaseEntity<ProductId> {

    private String name;
    private Money price;
    private int quantity;
    private boolean available;

    public void initializeProduct() {
        super.setId(new ProductId(UUID.randomUUID()));
    }

    // Getters
    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    public int getQuantity() {
        return quantity;
    }

    public boolean isAvailable() {
        return available;
    }

    // Builder
    private Product(Builder builder) {
        super.setId(builder.productId);
        this.name = builder.name;
        this.price = builder.price;
        this.quantity = builder.quantity;
        this.available = builder.available;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ProductId productId;
        private String name;
        private Money price;
        private int quantity;
        private boolean available;

        public Builder productId(ProductId productId) {
            this.productId = productId;
            return this;
        }

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder price(Money price) {
            this.price = price;
            return this;
        }

        public Builder quantity(int quantity) {
            this.quantity = quantity;
            return this;
        }

        public Builder available(boolean available) {
            this.available = available;
            return this;
        }

        public Product build() {
            return new Product(this);
        }
    }
}
