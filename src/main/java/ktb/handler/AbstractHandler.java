package ktb.handler;

import java.util.List;
import ktb.handler.context.ContextData;

/**
 * 연쇄 책임 패턴을 위한 추상 핸들러
 * @param <C> ContextData를 구현한 컨텍스트 타입
 */
public abstract class AbstractHandler<C extends ContextData<?>> {
    protected AbstractHandler<? super C> next;

    public boolean handle(C handlerContext) {
        if (next != null) {
            return next.handle(handlerContext);
        }

        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractHandler<? extends ContextData<?>>> T chainOf(T... handlers) {
        if (handlers == null || handlers.length == 0) {
            return null;
        }

        final T head = handlers[0];
        AbstractHandler tail = head;

        for (int i = 1; i < handlers.length; i++) {
            tail.next = handlers[i];
            tail = handlers[i];
        }

        return head;
    }

    @SuppressWarnings("unchecked")
    public static <C extends ContextData<?>>AbstractHandler<C> chainOf(
            AbstractHandler<C> origin,
            AbstractHandler<C>... targets
    ) {
        if (targets == null || targets.length == 0) {
            return origin;
        }

        final AbstractHandler<C> head = origin != null ? origin : targets[0];
        AbstractHandler tail = head;

        while (tail.next != null) {
            tail = tail.next;
        }

        int startIndex = origin != null ? 0 : 1;

        for (int i = startIndex; i < targets.length; i++) {
            AbstractHandler<C> target = targets[i];

            if (target == null) {
                continue;
            }

            tail.next = target;
            tail = target;
        }

        return head;
    }

    @SuppressWarnings("unchecked")
    public static <T extends AbstractHandler<? extends ContextData<?>>> T chainOf(List<T> handlers) {
        if (handlers == null || handlers.isEmpty()) {
            return null;
        }

        final T head = handlers.get(0);
        AbstractHandler tail = head;

        for (int i = 1; i < handlers.size(); i++) {
            tail.next = handlers.get(i);
            tail = handlers.get(i);
        }

        return head;
    }
}
