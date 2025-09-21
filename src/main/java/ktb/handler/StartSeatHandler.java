package ktb.handler;

public class StartSeatHandler extends AbstractHandler {
    @Override
    public boolean process(HandlerContext ctx) {
        ctx.seat.startUsage();

        return super.process(ctx);
    }
}
