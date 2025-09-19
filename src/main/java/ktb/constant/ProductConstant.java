package ktb.constant;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ProductConstant {
    DRINK("음료", 2000, 1),
    COFFEE("커피", 4000, 2),
    RAMEN("라면", 5000, 3);

    private final String name;
    private final int price;
    private final int choiceNumber;

    ProductConstant(String name, int price, int choiceNumber) {
        this.name = name;
        this.price = price;
        this.choiceNumber = choiceNumber;
    }

    public static ProductConstant from(int choiceNumber) {
        return Arrays.stream(values())
                .filter(product -> product.choiceNumber == choiceNumber)
                .findFirst()
                .orElseThrow(NoSuchFieldError::new);
    }

    public static ProductConstant from(String name) {

        return Arrays.stream(values())
                .filter(origin -> origin.name.equals(name))
                .findFirst()
                .orElseThrow(NoSuchFieldError::new);
    }

    public int getTotalPrice(int amount) {
        return price * amount;
    }

    public String toReceipt(int amount) {
        return String.format("%s x %d (%d)원", name, amount, this.getTotalPrice(amount));
    }

    public String toMenuString() {
        return String.format("%d: %s %d원 \n", choiceNumber, name, price);
    }

    public static String allMenuString() {
        return Arrays.stream(values())
                .map(ProductConstant::toMenuString)
                .collect(Collectors.joining("\n"));
    }
}
