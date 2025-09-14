package ktb.app;

import ktb.constant.ConfigConstant;
import ktb.constant.StringConstant;
import ktb.domain.OrderSeat;

import java.util.List;
import java.util.Scanner;
import ktb.domain.Product;
import ktb.domain.Seat;

public class SeatHandler {
    private final Scanner scanner;
    private final List<Seat> seats;

    public SeatHandler(List<Seat> seats, Scanner scanner) {
        this.scanner = scanner;
        this.seats = seats;
    }

    public void handleMenu(String input) {
        switch (input) {
            case "1" -> startSeat();
            case "2" -> addOrder();
            case "3" -> stopSeat();
            case "4" -> showSeats();
            case "5" -> System.out.println(StringConstant.EXIT_MESSAGE);
            default -> System.out.println(StringConstant.INVALID_INPUT);
        }
    }

    public boolean isExit(String input) {
        return "5".equals(input);
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
        // TODO:주문 추가
    }

    private void stopSeat() {
        // TODO:좌석 이용 중지
    }

    private void showSeats() {
        // TODO: 전체 좌석 현황 확인(이용 중 및 주문 내역 확인 필요)
    }
}
