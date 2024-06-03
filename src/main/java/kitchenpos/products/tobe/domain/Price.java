package kitchenpos.products.tobe.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.util.Objects;

@Embeddable
public class Price {

    @Column(name = "price", nullable = false)
    private int price;

    private Price() {
    }

    private Price(int price) {
        this.price = price;
    }

    public static Price of(int price) {
        if (price < 0) {
            throw new IllegalArgumentException("상품의 가격은 0원 이상이어야 한다.");
        }
        return new Price(price);
    }

    public int getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Price price1 = (Price) o;
        return price == price1.price;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(price);
    }
}
