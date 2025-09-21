package ktb.validator;

import ktb.constant.StringConstant;
import ktb.domain.seat.Seat;
import ktb.exception.ValidationException;

public class SeatNotInUseValidator implements Validator<Seat> {
    @Override
    public boolean validate(Seat seat) throws ValidationException {
        if (seat.isUse()) {
            throw new ValidationException(StringConstant.SEAT_IN_USE);
        }

        return true;
    }
}
