package ktb.task;

import java.util.Comparator;
import java.util.List;
import java.util.Scanner;

import ktb.constant.StringConstant;
import ktb.domain.menu.MenuEnum;
import ktb.domain.product.Product;
import ktb.domain.product.ProductEnum;
import ktb.domain.seat.Seat;
import ktb.exception.ExitSignalException;
import ktb.observer.Observer;
import ktb.request.ExitRequest;
import ktb.request.OrderRequest;
import ktb.request.SeatRequest;
import ktb.request.OutputConsoleRequest;
import ktb.util.Out;

public class RequestProducerTask implements Runnable {
    private final List<Seat> seats;
    private final Scanner scanner;
    private final Observer observer;
    
    private final Out out = Out.getInstance();

    public RequestProducerTask(List<Seat> seats, Scanner scanner, Observer observer) {
        this.seats = seats;
        this.scanner = scanner;
        this.observer = observer;
    }

    @Override
    public void run() {
        boolean running = true;

        try {
            while (running && !Thread.currentThread().isInterrupted()) {
                // 메뉴 출력
                observer.publishRequest(new OutputConsoleRequest());

                String input = scanner.nextLine();
                try {
                    MenuEnum menu = MenuEnum.from(input);

                    switch (menu) {
                        case START -> {
                            out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
                            int seatNumber = Integer.parseInt(scanner.nextLine()) - 1;

                            observer.publishRequest(new SeatRequest(seats.get(seatNumber), MenuEnum.START));
                        }
                        case ORDER -> {
                            out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
                            int seatNumber = Integer.parseInt(scanner.nextLine()) - 1;

                            out.printf(StringConstant.ORDER_MENU, ProductEnum.allMenuString());
                            int menuChoice = Integer.parseInt(scanner.nextLine());

                            out.print(StringConstant.ENTER_QUANTITY);
                            int quantity = Integer.parseInt(scanner.nextLine());

                            Product product = new Product(ProductEnum.from(menuChoice), quantity);
                            observer.publishRequest(new OrderRequest(seats.get(seatNumber), product));
                        }
                        case STOP -> {
                            out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
                            int seatNumber = Integer.parseInt(scanner.nextLine()) - 1;

                            observer.publishRequest(new SeatRequest(seats.get(seatNumber), MenuEnum.STOP));
                        }
                        case PAUSE -> {
                            out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
                            int seatNumber = Integer.parseInt(scanner.nextLine()) - 1;

                            observer.publishRequest(new SeatRequest(seats.get(seatNumber), MenuEnum.PAUSE));
                        }
                        case RESTART -> {
                            out.printf(StringConstant.ENTER_SEAT_NUMBER, seats.size());
                            int seatNumber = Integer.parseInt(scanner.nextLine()) - 1;

                            observer.publishRequest(new SeatRequest(seats.get(seatNumber), MenuEnum.RESTART));
                        }
                        case SHOW_SEAT -> {
                            seats.sort(Comparator.comparing(Seat::getSeatNumber));

                            for (Seat seat : seats) {
                                String seatInfo = String.format(
                                        "좌석 %d : %s | 주문: %s",
                                        seat.getSeatNumber(),
                                        seat.getSeatStatus(),
                                        seat.getOrderSummary()
                                );

                                observer.publishRequest(new OutputConsoleRequest(seatInfo));
                            }
                        }
                        case EXIT -> {
                            observer.publishRequest(new OutputConsoleRequest(StringConstant.EXIT_MESSAGE));
                            observer.publishRequest(new ExitRequest());
                            running = false;

                            throw new InterruptedException();
                        }
                    }
                } catch (NumberFormatException e) {
                    observer.publishRequest(new OutputConsoleRequest(StringConstant.INVALID_INPUT));
                }
            }
        } catch(ExitSignalException | InterruptedException ie) {
            scanner.close();
            Thread.currentThread().interrupt();
        }
    }
}
