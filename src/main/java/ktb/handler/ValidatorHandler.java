package ktb.handler;

import ktb.exception.ValidationException;
import ktb.validator.Validator;
import ktb.util.Out;

public class ValidatorHandler<T> extends AbstractHandler {
    private final Validator<T> validator;
    private final T target;

    private final Out out = Out.getInstance();

    public ValidatorHandler(Validator<T> validator, T target) {
        this.validator = validator;
        this.target = target;
    }

    @Override
    public boolean process(HandlerContext ctx) {
        try {
            validator.validate(target);
            return super.process(ctx);
        } catch (ValidationException e) {
            out.println(e.getMessage());
            return false;
        }
    }
}
