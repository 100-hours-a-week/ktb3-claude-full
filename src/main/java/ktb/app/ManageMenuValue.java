package ktb.app;

public enum ManageMenuValue {
    START("1", "좌석 사용 시작"),
    ORDER("2", "주문 추가"),
    STOP("3", "좌석 사용 종료"),
    SHOW_SEAT("4", "좌석 현황 보기"),
    EXIT("5", "종료");

    private final String value;
    private final String description;

    ManageMenuValue(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ManageMenuValue from(String input) {
        for (ManageMenuValue menu : values()) {
            if (menu.value.equals(input)) {
                return menu;
            }
        }
        return null;
    }

    public static String menuString() {
        StringBuilder sb = new StringBuilder();

        for (ManageMenuValue menu : values()) {
            sb.append(menu.value)
                    .append(": ")
                    .append(menu.description)
                    .append("  ");
        }

        sb.append("\n선택: ");
        return sb.toString();
    }

    public static boolean isExit(String input) {
        return EXIT.value.equals(input);
    }
}
