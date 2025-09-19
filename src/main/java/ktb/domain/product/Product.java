package ktb.domain;

import ktb.constant.ProductConstant;

public class Product {
    private final ProductConstant product;
    private final int amount;

    public Product(String name, int amount) {
        this.product = ProductConstant.from(name);
        this.amount = amount;
    }

    public Product(ProductConstant product, int amount) {
        this.product = product;
        this.amount = amount;
    }

    public int getTotalPrice() {
        return product.getTotalPrice(this.amount);
    }

    public String toReceipt() {
        return product.toReceipt(this.amount);
    }
}
