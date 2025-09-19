package ktb.app;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import ktb.constant.ConfigConstant;
import ktb.constant.StringConstant;
import ktb.domain.OrderSeat;
import ktb.domain.Seat;

public class PcCafeApp {
    public static void main(String[] args) {
        List<Seat> seats = new ArrayList<>(ConfigConstant.MAX_SEATS);

        for (int i = 1; i < ConfigConstant.MAX_SEATS + 1; i++) {
            seats.add(new OrderSeat(i));
        }

        Scanner scanner = new Scanner(System.in);
        SeatHandler seatHandler = new SeatHandler(seats, scanner);

        System.out.println(StringConstant.TITLE);

        Thread seatThread = new Thread(seatHandler);

        seatThread.start();
    }
}
