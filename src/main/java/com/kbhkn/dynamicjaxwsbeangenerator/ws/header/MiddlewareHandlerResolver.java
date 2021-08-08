package com.kbhkn.dynamicjaxwsbeangenerator.ws.header;

import java.util.List;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.HandlerResolver;
import javax.xml.ws.handler.PortInfo;
import lombok.Setter;

/**
 * Manages headers.
 *
 * @author Hakan KABASAKAL, 08-Aug-21
 */
@SuppressWarnings("rawtypes")
@Setter
public class MiddlewareHandlerResolver implements HandlerResolver {
    private List<Handler> handlerList;

    @Override
    public List<Handler> getHandlerChain(PortInfo portInfo) {
        return handlerList;
    }
}
