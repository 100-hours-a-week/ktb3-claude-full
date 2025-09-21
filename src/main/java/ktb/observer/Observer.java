package ktb.observer;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import ktb.request.Request;

public class Observer {
    private final BlockingQueue<Request> queue = new LinkedBlockingQueue<>();

    public void publishRequest(Request request) throws InterruptedException {
        queue.put(request);
    }

    public Request consumeRequest() throws InterruptedException {
        return queue.take();
    }
}
