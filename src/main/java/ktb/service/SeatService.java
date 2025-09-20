package ktb.service;

import ktb.domain.menu.MenuEnum;
import ktb.domain.product.ProductEnum;
import ktb.constant.StringConstant;

import java.util.List;
import java.util.Scanner;
import ktb.domain.product.Product;
import ktb.domain.seat.Seat;

public class SeatService implements Runnable{
    private final Scanner scanner;
    private final List<Seat> seats;

    public SeatService(List<Seat> seats, Scanner scanner) {
        this.scanner = scanner;
        this.seats = seats;
    }

    @Override
    public void run() {
        boolean running = true;

        while (running) {
            System.out.print(MenuEnum.menuString());
            String input = scanner.nextLine();

            this.handleMenu(input);
            running = !MenuEnum.isExit(input);
        }

        scanner.close();
    }

    public void handleMenu(String input) {
        MenuEnum menuValue = MenuEnum.from(input);

        if (menuValue == null) {
            System.out.println(StringConstant.INVALID_INPUT);

            return;
        }

        switch (menuValue) {
            case START -> startSeat();
            case ORDER -> addOrder();
            case STOP -> stopSeat();
            case PAUSE -> pauseSeat();
            case RESTART -> restartSeat();
            case SHOW_SEAT -> showSeats();
            case EXIT -> System.out.println(StringConstant.EXIT_MESSAGE);
            default -> System.out.println(StringConstant.INVALID_INPUT);
        }
    }

    private void startSeat() {
        System.out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
        int seatNumber = Integer.parseInt(scanner.nextLine()) - 1; // 좌석번호 1부터 시작

        Seat seat = seats.get(seatNumber);
        if (seat.isUse()) {
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

        if (!seat.isUse()) {
            System.out.println(StringConstant.SEAT_NOT_IN_USE);
            System.out.println();
            return;
        }

        if (!seat.canOrder()) {
            System.out.println("주문이 불가능한 좌석입니다.");
            System.out.println();
            return;
        }

        System.out.printf(StringConstant.ORDER_MENU, ProductEnum.allMenuString());
        int menuChoice = Integer.parseInt(scanner.nextLine());

        System.out.print(StringConstant.ENTER_QUANTITY);
        int quantity = Integer.parseInt(scanner.nextLine());

        Product product = new Product(ProductEnum.from(menuChoice), quantity);

        seat.addProduct(product);

        System.out.println(StringConstant.ORDER_ADDED);
    }

    private void stopSeat() {
        System.out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
        int seatNumber = Integer.parseInt(scanner.nextLine()) - 1; // 좌석번호 1부터 시작

        Seat seat = seats.get(seatNumber);
        if (seat.isUse()) {
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

    private void pauseSeat() {
        System.out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
        int seatNumber = Integer.parseInt(scanner.nextLine()) - 1; // 좌석번호 1부터 시작

        Seat seat = seats.get(seatNumber);

        if (seat.isInUse()) {
            seat.pauseTemporary();

            System.out.println(StringConstant.SEAT_PAUSED);
        } else {
            System.out.println(StringConstant.SEAT_NOT_IN_USE);
        }
    }

    private void restartSeat() {
        System.out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
        int seatNumber = Integer.parseInt(scanner.nextLine()) - 1; // 좌석번호 1부터 시작

        Seat seat = seats.get(seatNumber);

        if (!seat.isInUse()) {
            seat.restart();

            System.out.println(StringConstant.SEAT_RESTARTED);
        } else {
            System.out.println(StringConstant.SEAT_IN_USE);
        }
    }

    private void showSeats() {
        StringBuilder sb = new StringBuilder();
        for (Seat seat : seats) {
            sb.append("좌석 ")
                    .append(seat.getSeatNumber())
                    .append(" : ")
                    .append(seat.isUse() ? "사용 중" : "빈 좌석");

            sb.append(" | 주문: ")
                    .append(seat.getOrderSummary());
            sb.append("\n");
        }
        System.out.println(sb);
    }
}
