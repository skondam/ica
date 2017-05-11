package de.jkitberatung.ica.wsh.events;

import com4j.*;

/**
 * _ISessionEvents Interface
 */
@IID("{24FD31DB-3560-4C78-8950-30F03352D830}")
public abstract class _ISessionEvents {
    /**
     * method OnWindowCreate
     */
    @DISPID(1)
    public void onWindowCreate(
        de.jkitberatung.ica.wsh.IWindow window) {
            throw new UnsupportedOperationException();
    }

    /**
     * method OnWindowDestroy
     */
    @DISPID(2)
    public void onWindowDestroy(
        de.jkitberatung.ica.wsh.IWindow window) {
            throw new UnsupportedOperationException();
    }

    /**
     * method OnPingAck
     */
    @DISPID(4)
    public void onPingAck(
        java.lang.String pingInfo,
        int roundTripTime) {
            throw new UnsupportedOperationException();
    }

    /**
     * method OnWindowForeground
     */
    @DISPID(5)
    public void onWindowForeground(
        int windowID) {
            throw new UnsupportedOperationException();
    }

}
