package ktb.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import ktb.constant.ConfigConstant;
import ktb.constant.StringConstant;
import ktb.domain.seat.BasicSeat;
import ktb.domain.seat.Seat;
import ktb.service.SeatService;

public class PcCafeApp {
    public static void main(String[] args) {
        List<Seat> seats = new ArrayList<>(ConfigConstant.MAX_SEATS);

        for (int i = 1; i < ConfigConstant.MAX_SEATS + 1; i++) {
            seats.add(new BasicSeat(i));
        }

        Scanner scanner = new Scanner(System.in);
        SeatService seatService = new SeatService(seats, scanner);

        System.out.println(StringConstant.TITLE);

        Thread seatThread = new Thread(seatService);

        seatThread.start();
    }
}
