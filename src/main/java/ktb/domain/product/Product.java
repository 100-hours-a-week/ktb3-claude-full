package ktb.domain.product;

public class Product {
    private final ProductEnum product;
    private final int amount;

    public Product(String name, int amount) {
        this.product = ProductEnum.from(name);
        this.amount = amount;
    }

    public Product(ProductEnum product, int amount) {
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
