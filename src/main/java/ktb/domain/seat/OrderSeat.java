package ktb.domain;

import java.util.ArrayList;
import java.util.List;

public class OrderSeat extends CustomerSeat {
    private final List<Product> products = new ArrayList<>();

    public OrderSeat(int seatNumber) {
        super(seatNumber);
    }

    public void addProduct(Product product) {
        products.add(product);
    }

    public int getProductTotal() {
        return products.stream().mapToInt(Product::getTotalPrice).sum();
    }

    @Override
    public boolean canOrder() { return true; }

    @Override
    public int getUsageFee() {
        return super.getUsageFee() + getProductTotal();
    }

    @Override
    public int getOrderSummaryPee() {
        if (products.isEmpty()) return 0;

        return products.stream()
                .mapToInt(Product::getTotalPrice)
                .sum();
    }

    @Override
    public String getOrderSummary() {
        if (products.isEmpty()) {
            return "없음";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < products.size(); i++) {
            sb.append(products.get(i));
            if (i < products.size() - 1) {
                sb.append(", ");
            }
        }
        return sb.toString();
    }

}
