package ktb.validator;

import ktb.constant.StringConstant;
import ktb.domain.seat.Seat;
import ktb.exception.ValidationException;

public class SeatCanPauseValidator implements Validator<Seat> {
    @Override
    public boolean validate(Seat seat) throws ValidationException {
        if (seat.isPaused()) {
            throw new ValidationException(StringConstant.SEAT_IN_PAUSE);
        }

        if (!seat.isUse()) {
            throw new ValidationException(StringConstant.SEAT_NOT_IN_USE);
        }

        return true;
    }
}
