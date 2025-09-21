package ktb.handler;

import ktb.util.Out;

public class PrintHandler extends AbstractHandler {
    private final String message;

    private final Out out = Out.getInstance();

    public PrintHandler(String message) {
        this.message = message;
    }

    @Override
    public boolean process(HandlerContext ctx) {
        out.println(message);

        return super.process(ctx);
    }
}
