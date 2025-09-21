package ktb.request;

import ktb.constant.StringConstant;
import ktb.domain.menu.MenuEnum;
import ktb.handler.AbstractHandler;
import ktb.handler.HandlerContext;
import ktb.handler.PrintHandler;

public class OutputConsoleRequest implements Request {
    private final String message;

    public OutputConsoleRequest() {
        this.message = StringConstant.TITLE + "\n" + MenuEnum.menuString();
    }

    public OutputConsoleRequest(String message) {
        this.message = message;
    }

    @Override
    public void processRequest() {
        AbstractHandler handler = new PrintHandler(message);
        handler.process(new HandlerContext(null, null));
    }
}
