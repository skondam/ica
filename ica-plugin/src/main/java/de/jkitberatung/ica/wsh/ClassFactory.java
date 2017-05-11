package de.jkitberatung.ica.wsh  ;

import com4j.*;

/**
 * Defines methods to create COM objects
 */
public abstract class ClassFactory {
    private ClassFactory() {} // instanciation is not allowed


    /**
     * Citrix ICA Client
     */
    public static de.jkitberatung.ica.wsh.IICAClient createICAClient() {
        return COM4J.createInstance( de.jkitberatung.ica.wsh.IICAClient.class, "{238F6F83-B8B4-11CF-8771-00A024541EE3}" );
    }

    /**
     * ICA Client Properties
     */
    public static Com4jObject createICAClientProp() {
        return COM4J.createInstance( Com4jObject.class, "{238F6F85-B8B4-11CF-8771-00A024541EE3}" );
    }
}
