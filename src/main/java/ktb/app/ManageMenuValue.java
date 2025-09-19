package ktb.app;

import java.util.Arrays;
import java.util.stream.Collectors;

public enum ManageMenuValue {
    START("1", "좌석 사용 시작"),
    ORDER("2", "주문 추가"),
    STOP("3", "좌석 사용 종료"),
    PAUSE("4", "좌석 일시 중지"),
    RESTART("5", "재시작"),
    SHOW_SEAT("6", "좌석 현황 보기"),
    EXIT("7", "종료");

    private final String value;
    private final String description;

    ManageMenuValue(String value, String description) {
        this.value = value;
        this.description = description;
    }

    public static ManageMenuValue from(String input) {
        return Arrays.stream(values())
                .filter(menu -> menu.value.equals(input))
                .findFirst()
                .orElseThrow(NoSuchFieldError::new);
    }

    public static String menuString() {
        public static String menuString() {
            String menuItems = Arrays.stream(values())
                    .map(menu -> menu.value + ": " + menu.description + "  ")
                    .collect(Collectors.joining());

            return menuItems + "\n선택: ";
        }
    }

    public static boolean isExit(String input) {
        return EXIT.value.equals(input);
    }
}
