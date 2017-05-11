package de.jkitberatung.ica.wsh  ;

import com4j.*;

/**
 * IMouse Interface
 */
@IID("{02093274-7B69-4FEB-B7FD-3A010561A5F3}")
public interface IMouse extends Com4jObject {
    /**
     * method SendMouseDown
     */
    @VTID(7)
    void sendMouseDown(
        int buttonId,
        int modifiers,
        int xPos,
        int yPos);

    /**
     * method SendMouseUp
     */
    @VTID(8)
    void sendMouseUp(
        int buttonId,
        int modifiers,
        int xPos,
        int yPos);

    /**
     * method SendMouseMove
     */
    @VTID(9)
    void sendMouseMove(
        int buttonId,
        int modifiers,
        int xPos,
        int yPos);

}
