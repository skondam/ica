package de.jkitberatung.ica.wsh  ;

import com4j.*;

/**
 * IWindows Interface
 */
@IID("{9B371833-5E4F-4B72-A8F6-CB8E762999F4}")
public interface IWindows extends Com4jObject,Iterable<Com4jObject> {
    @VTID(7)
    @DefaultMethod
    de.jkitberatung.ica.wsh.IWindow item(
        int n);

    @VTID(8)
    java.util.Iterator<Com4jObject> iterator();

    @VTID(9)
    int count();

}
