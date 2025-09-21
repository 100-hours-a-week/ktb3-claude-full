package ktb.validator;

import ktb.exception.ValidationException;

public interface Validator<T> {
    boolean validate(T input) throws ValidationException;
}
