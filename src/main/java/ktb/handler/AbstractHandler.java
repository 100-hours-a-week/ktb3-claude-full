package ktb.handler;

public abstract class AbstractHandler {
    protected AbstractHandler next;

    public void setNext(AbstractHandler next) {
        this.next = next;
    }

    public boolean process(HandlerContext ctx) {
        if (next != null) {
            return next.process(ctx);
        }

        return true;
    }

    public static AbstractHandler buildHandler(AbstractHandler origin, AbstractHandler... targets) {
        if (targets == null || targets.length == 0) {
            return origin;
        }

        AbstractHandler head = origin;

        for (AbstractHandler target : targets) {
            if (head == null) {
                head = target;
            } else {
                AbstractHandler current = head;

                while (current.next != null) {
                    current = current.next;
                }

                current.setNext(target);
            }
        }

        return head;
    }
}
