package de.jkitberatung.ica.wsh  ;

import com4j.*;

/**
 * ISession Interface
 */
@IID("{4A502C16-CFAE-4BB0-B1F9-93ACADDA57BB}")
public interface ISession extends Com4jObject {
    /**
     * property TopLevelWindows
     */
    @VTID(7)
    de.jkitberatung.ica.wsh.IWindows topLevelWindows();

    @VTID(7)
    @ReturnValue(defaultPropertyThrough={de.jkitberatung.ica.wsh.IWindows.class})
    de.jkitberatung.ica.wsh.IWindow topLevelWindows(
        int n);

    /**
     * property Mouse
     */
    @VTID(8)
    de.jkitberatung.ica.wsh.IMouse mouse();

    /**
     * property Keyboard
     */
    @VTID(9)
    de.jkitberatung.ica.wsh.IKeyboard keyboard();

    /**
     * property ForegroundWindow
     */
    @VTID(10)
    de.jkitberatung.ica.wsh.IWindow foregroundWindow();

    /**
     * property ReplayMode
     */
    @VTID(11)
    boolean replayMode();

    /**
     * property ReplayMode
     */
    @VTID(12)
    void replayMode(
        boolean pVal);

    /**
     * method CreateFullScreenShot
     */
    @VTID(13)
    de.jkitberatung.ica.wsh.IScreenShot createFullScreenShot();

    /**
     * method CreateScreenShot
     */
    @VTID(14)
    de.jkitberatung.ica.wsh.IScreenShot createScreenShot(
        int x,
        int y,
        int width,
        int height);

    /**
     * method SendPingRequest
     */
    @VTID(15)
    void sendPingRequest(
        java.lang.String pingInfo);

}
