package ktb.handler;

public class RestartSeatHandler extends AbstractHandler {
    @Override
    public boolean process(HandlerContext ctx) {
        ctx.seat.restart();

        return super.process(ctx);
    }
}
