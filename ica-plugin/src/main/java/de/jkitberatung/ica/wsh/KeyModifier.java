package de.jkitberatung.ica.wsh  ;

import com4j.*;

/**
 * Key modifier buttons
 */
public enum KeyModifier implements ComEnum {
    KeyModifierShift(1),
    KeyModifierControl(2),
    KeyModifierAlt(4),
    KeyModifierExtended(8),
    ;

    private final int value;
    KeyModifier(int value) { this.value=value; }
    public int comEnumValue() { return value; }
}
