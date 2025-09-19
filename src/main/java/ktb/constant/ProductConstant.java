package ktb.constant;

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
        for (ProductConstant product : values()) {
            if (product.choiceNumber == choiceNumber) {
                return product;
            }
        }
        return null;
    }

    public static ProductConstant from(String name) {
        for (ProductConstant product : values()) {
            if (product.name.equals(name)) {
                return product;
            }
        }
        return null;
    }

    public String getName() {
        return name;
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
        StringBuilder sb = new StringBuilder();
        
        for (ProductConstant item : values()) {
            sb.append(item.toMenuString()).append("\n");
        }
        
        return sb.toString();
    }
}
