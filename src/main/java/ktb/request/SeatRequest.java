package ktb.request;

import ktb.validator.SeatInUseValidator;
import ktb.validator.SeatNotInUseValidator;
import ktb.constant.StringConstant;
import ktb.domain.menu.MenuEnum;
import ktb.domain.seat.Seat;
import ktb.handler.AbstractHandler;
import ktb.handler.BillSeatHandler;
import ktb.handler.HandlerContext;
import ktb.handler.PauseSeatHandler;
import ktb.handler.PrintHandler;
import ktb.handler.RestartSeatHandler;
import ktb.handler.StartSeatHandler;
import ktb.handler.StopSeatHandler;
import ktb.handler.ValidatorHandler;

public class SeatRequest implements Request {
    private final Seat seat;
    private final MenuEnum action;

    public SeatRequest(Seat seat, MenuEnum action) {
        this.seat = seat;
        this.action = action;
    }

    @Override
    public void processRequest() {
        AbstractHandler handlerChain = null;

        switch (action) {
            case START -> handlerChain = AbstractHandler.buildHandler(null,
                    new ValidatorHandler<>(new SeatNotInUseValidator(), seat),
                    new StartSeatHandler(),
                    new PrintHandler(StringConstant.SEAT_STARTED));

            case STOP -> handlerChain = AbstractHandler.buildHandler(null,
                    new ValidatorHandler<>(new SeatInUseValidator(), seat),
                    new BillSeatHandler(),
                    new StopSeatHandler(),
                    new PrintHandler(StringConstant.BILL_PRINTED));

            case PAUSE -> handlerChain = AbstractHandler.buildHandler(null,
                    new PauseSeatHandler(),
                    new PrintHandler(StringConstant.SEAT_PAUSED));

            case RESTART -> handlerChain = AbstractHandler.buildHandler(null,
                    new RestartSeatHandler(),
                    new PrintHandler(StringConstant.SEAT_RESTARTED));
        }

        if (handlerChain != null) {
            handlerChain.process(new HandlerContext(seat));
        }
    }
}
