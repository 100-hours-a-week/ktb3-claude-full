package ktb.task;

import ktb.exception.ExitSignalException;
import ktb.observer.Observer;
import ktb.request.Request;

public class RequestConsumerTask implements Runnable {
    private final Observer observer;

    public RequestConsumerTask(Observer observer) {
        this.observer = observer;
    }

    @Override
    public void run() {
        try {
            while (!Thread.currentThread().isInterrupted()) {
                Request req = observer.consumeRequest();
                req.processRequest();
            }
        } catch(ExitSignalException | InterruptedException ese) {
            Thread.currentThread().interrupt();
        }
    }
}
