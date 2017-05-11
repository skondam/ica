package de.jkitberatung.ica.wsh.events;

import com4j.*;

/**
 * _IKeyboardEvents Interface
 */
@IID("{8A5961DF-314E-4B7C-B57F-AAF35EA33079}")
public abstract class _IKeyboardEvents {
    /**
     * method OnKeyUp
     */
    @DISPID(1)
    public void onKeyUp(
        int keyId,
        int modifierState) {
            throw new UnsupportedOperationException();
    }

    /**
     * method OnKeyDown
     */
    @DISPID(2)
    public void onKeyDown(
        int keyId,
        int modifierState) {
            throw new UnsupportedOperationException();
    }

}
