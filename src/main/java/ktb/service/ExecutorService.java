package ktb.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ThreadPoolExecutor;

import ktb.domain.seat.Seat;
import ktb.observer.Observer;
import ktb.task.RequestConsumerTask;
import ktb.task.RequestProducerTask;
import ktb.util.ThreadPoolUtil;

public class ExecutorService {
    private final ThreadPoolExecutor executor;
    private final Observer observer;


    public ExecutorService() {
        this.executor = ThreadPoolUtil.createPool(3, 5, 10, null);
        this.observer = new Observer();
    }

    public void start(List<Seat> seats, Scanner scanner) {

        executor.submit(new RequestProducerTask(seats, scanner, observer));
        executor.submit(new RequestConsumerTask(observer));
    }
}
