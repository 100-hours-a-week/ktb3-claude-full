package ktb.validator;

import ktb.constant.StringConstant;
import ktb.domain.product.Product;
import ktb.exception.ValidationException;

public class OrderAmountValidator implements Validator<Product> {
    @Override
    public boolean validate(Product product) throws ValidationException {
        if (product.getAmount() <= 0) {
            throw new ValidationException(StringConstant.INVALID_INPUT);
        }
        return true;
    }
}

