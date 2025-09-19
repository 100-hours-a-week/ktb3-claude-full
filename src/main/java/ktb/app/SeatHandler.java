package ktb.app;

import ktb.constant.ConfigConstant;
import ktb.constant.StringConstant;

import java.util.List;
import java.util.Scanner;
import ktb.domain.Product;
import ktb.domain.Seat;

public class SeatHandler implements Runnable{
    private final Scanner scanner;
    private final List<Seat> seats;

    public SeatHandler(List<Seat> seats, Scanner scanner) {
        this.scanner = scanner;
        this.seats = seats;
    }

    @Override
    public void run() {
        boolean running = true;

        while (running) {
            System.out.print(ManageMenuValue.menuString());
            String input = scanner.nextLine();

            this.handleMenu(input);
            running = !ManageMenuValue.isExit(input);
        }
    }

    public void handleMenu(String input) {
        ManageMenuValue menuValue = ManageMenuValue.from(input);

        if (menuValue == null) {
            System.out.println(StringConstant.INVALID_INPUT);

            return;
        }

        switch (menuValue) {
            case START -> startSeat();
            case ORDER -> addOrder();
            case STOP -> stopSeat();
            case SHOW_SEAT -> showSeats();
            case EXIT -> System.out.println(StringConstant.EXIT_MESSAGE);
            default -> System.out.println(StringConstant.INVALID_INPUT);
        }
    }

    private void startSeat() {
        System.out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
        int seatNumber = Integer.parseInt(scanner.nextLine()) - 1; // 좌석번호 1부터 시작

        Seat seat = seats.get(seatNumber);
        if (seat.isInUse()) {
            System.out.println(StringConstant.SEAT_ALREADY_IN_USE);
            System.out.println();
        } else {
            seat.startUsage();
            System.out.println(StringConstant.SEAT_STARTED);
            System.out.println();
        }
    }

    private void addOrder() {
        System.out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
        int seatNumber = Integer.parseInt(scanner.nextLine()) - 1; // 좌석번호 1부터 시작
        Seat seat = seats.get(seatNumber);

        if (!seat.isInUse()) {
            System.out.println(StringConstant.SEAT_NOT_IN_USE);
            System.out.println();
            return;
        }

        if (!seat.canOrder()) {
            System.out.println("주문이 불가능한 좌석입니다.");
            System.out.println();
            return;
        }

        System.out.printf(StringConstant.ORDER_MENU
                , ConfigConstant.PRICE_DRINK
                , ConfigConstant.PRICE_COFFEE
                , ConfigConstant.PRICE_CUPRAMEN);
        int menuChoice = Integer.parseInt(scanner.nextLine());

        System.out.print(StringConstant.ENTER_QUANTITY);
        int quantity = Integer.parseInt(scanner.nextLine());

        Product product;
        switch (menuChoice) {
            case 1 -> product = new Product("음료", ConfigConstant.PRICE_DRINK, quantity);
            case 2 -> product = new Product("커피", ConfigConstant.PRICE_COFFEE, quantity);
            case 3 -> product = new Product("컵라면", ConfigConstant.PRICE_CUPRAMEN, quantity);
            default -> {
                System.out.println(StringConstant.INVALID_INPUT);
                return;
            }
        }

        seat.addProduct(product);
        System.out.println(StringConstant.ORDER_ADDED);
    }

    private void stopSeat() {
        System.out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
        int seatNumber = Integer.parseInt(scanner.nextLine()) - 1; // 좌석번호 1부터 시작

        Seat seat = seats.get(seatNumber);
        if (seat.isInUse()) {
            int fee = seat.getUsageFee();
            fee += seat.getOrderSummaryPee();

            seat.stopUsage();

            StringBuilder sb = new StringBuilder();
            sb.append(String.format(StringConstant.FEE_MESSAGE, seatNumber, fee))
                    .append("\n");
            sb.append(StringConstant.BILL_PRINTED)
                    .append("\n");

            System.out.println(sb);
        } else {
            System.out.println(StringConstant.SEAT_NOT_IN_USE);
        }
    }

    private void showSeats() {
        StringBuilder sb = new StringBuilder();
        for (Seat seat : seats) {
            sb.append("좌석 ")
                    .append(seat.getSeatNumber())
                    .append(" : ")
                    .append(seat.isInUse() ? "사용 중" : "빈 좌석");

            sb.append(" | 주문: ")
                    .append(seat.getOrderSummary());
            sb.append("\n");
        }
        System.out.println(sb);
    }
}
