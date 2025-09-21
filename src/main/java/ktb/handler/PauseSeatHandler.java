package ktb.handler;

public class PauseSeatHandler extends AbstractHandler {
    @Override
    public boolean process(HandlerContext ctx) {
        ctx.seat.pauseTemporary();

        return super.process(ctx);
    }
}
