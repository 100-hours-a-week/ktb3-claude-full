package ktb.handler;

import ktb.domain.product.Product;
import ktb.domain.seat.Seat;

public class HandlerContext {
    protected Product product;
    protected Seat seat;

    public HandlerContext(Product product, Seat seat) {
        this.product = product;
        this.seat = seat;
    }

    public HandlerContext(Seat seat) {
        this.seat = seat;
        this.product = null;
    }
}
