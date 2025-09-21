package ktb.request;

import ktb.validator.OrderAmountValidator;
import ktb.validator.SeatInUseValidator;
import ktb.constant.StringConstant;
import ktb.domain.product.Product;
import ktb.domain.seat.Seat;
import ktb.handler.AbstractHandler;
import ktb.handler.AddOrderHandler;
import ktb.handler.HandlerContext;
import ktb.handler.PrintHandler;
import ktb.handler.ValidatorHandler;

public class OrderRequest implements Request {
    private final Seat seat;
    private final Product product;

    public OrderRequest(Seat seat, Product product) {
        this.seat = seat;
        this.product = product;
    }

    @Override
    public void processRequest() {
        AbstractHandler handlerChain =
                AbstractHandler.buildHandler(null,
                        new ValidatorHandler<>(new SeatInUseValidator(), seat),
                        new ValidatorHandler<>(new OrderAmountValidator(), product),
                        new AddOrderHandler(),
                        new PrintHandler(StringConstant.ORDER_ADDED));

        handlerChain.process(new HandlerContext(this.product, this.seat));
    }
}
