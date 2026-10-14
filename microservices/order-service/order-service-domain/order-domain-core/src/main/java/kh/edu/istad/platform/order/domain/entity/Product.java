package kh.edu.istad.platform.order.domain.entity;

import kh.edu.istad.common.domain.entity.BaseEntity;
import kh.edu.istad.common.domain.valueobject.Money;
import kh.edu.istad.common.domain.valueobject.ProductId;
import kh.edu.istad.platform.order.domain.exception.OrderDomainException;

public class Product extends BaseEntity<ProductId> {

    private String name;
    private Money price;

    public void validate() {
        if (super.getId() == null) {
            throw new OrderDomainException("Product ID is required");
        }
        if (name == null || name.isBlank()) {
            throw new OrderDomainException("Product name is required");
        }
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Product price must be greater than zero");
        }
    }

    public void updateWithConfirmedNameAndPrice(String name, Money price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Money getPrice() {
        return price;
    }

    private Product(Builder builder) {
        super.setId(builder.id);
        this.name = builder.name;
        this.price = builder.price;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private ProductId id;
        private String name;
        private Money price;

        public Builder id(ProductId id) {
            this.id = id;
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

        public Product build() {
            return new Product(this);
        }
    }
}
