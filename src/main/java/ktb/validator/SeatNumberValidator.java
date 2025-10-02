package ktb.validator;

import java.util.List;
import ktb.domain.seat.Seat;
import ktb.exception.ValidationException;

public class SeatNumberValidator implements Validator<Integer> {
    private final List<Seat> seats;

    public SeatNumberValidator(List<Seat> seats) {
        this.seats = seats;
    }

    @Override
    public boolean validate(Integer seatNumber) throws ValidationException {
        if (seatNumber < 0 || seatNumber >= seats.size()) {
            throw new ValidationException("좌석 번호가 범위를 벗어났습니다: " + (seatNumber + 1));
        }
        return true;
    }
}

