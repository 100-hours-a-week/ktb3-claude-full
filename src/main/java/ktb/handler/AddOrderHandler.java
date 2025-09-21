package ktb.handler;

import ktb.domain.product.Product;
import ktb.domain.seat.Seat;

public class AddOrderHandler extends AbstractHandler {
    @Override
    public boolean process(HandlerContext ctx) {
        Seat seat = ctx.seat;
        Product product = ctx.product;

        seat.addProduct(product);
        return super.process(ctx);
    }
}
