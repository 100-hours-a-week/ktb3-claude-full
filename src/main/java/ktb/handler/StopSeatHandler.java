package ktb.handler;

public class StopSeatHandler extends AbstractHandler {
    @Override
    public boolean process(HandlerContext ctx) {
        ctx.seat.stopUsage();

        return super.process(ctx);
    }
}
