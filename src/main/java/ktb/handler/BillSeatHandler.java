package ktb.handler;

import ktb.constant.StringConstant;
import ktb.domain.seat.Seat;
import ktb.util.Out;

public class BillSeatHandler extends AbstractHandler {
    private final Out out = Out.getInstance();

    @Override
    public boolean process(HandlerContext ctx) {
        Seat seat = ctx.seat;

        int fee = seat.getUsageFee() + seat.getOrderSummaryPee();
        String msg = String.format(StringConstant.FEE_MESSAGE, seat.getSeatNumber(), fee);

        out.println(msg);

        return super.process(ctx);
    }
}
