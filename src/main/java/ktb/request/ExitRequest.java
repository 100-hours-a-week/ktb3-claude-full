package ktb.request;

import ktb.exception.ExitSignalException;

public class ExitRequest implements Request{
    @Override
    public void processRequest() throws ExitSignalException {
        throw new ExitSignalException("Exit Request");
    }
}
