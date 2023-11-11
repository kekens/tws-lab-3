package ru.kekens;


import ru.kekens.exception.ThrottlingException;
import ru.kekens.exception.ThrottlingFault;

import javax.xml.namespace.QName;
import javax.xml.ws.handler.MessageContext;
import javax.xml.ws.handler.soap.SOAPHandler;
import javax.xml.ws.handler.soap.SOAPMessageContext;
import java.util.Collections;
import java.util.Set;

public class ThrottlingControl implements SOAPHandler<SOAPMessageContext> {

    private static final int MAX_REQUESTS = 3;
    private static final long PERIOD_MILLI_SECONDS = 1000;
    private static long lastResetTime = System.currentTimeMillis();
    private static int requestCount = 0;

    @Override
    public boolean handleMessage(SOAPMessageContext context) {
        boolean isOutbound = (Boolean) context.get(MessageContext.MESSAGE_OUTBOUND_PROPERTY);

        if (!isOutbound) {
            synchronized (ThrottlingControl.class) {
                long currentTime = System.currentTimeMillis();
                if (currentTime - lastResetTime > PERIOD_MILLI_SECONDS) {
                    // Сброс счетчика запросов, если прошло больше PERIOD_SECONDS секунд
                    lastResetTime = currentTime;
                    requestCount = 0;
                }

                if (requestCount >= MAX_REQUESTS) {
                    throw new ThrottlingException("Кол-во запросов - " + ++requestCount +
                            ". Максимально возможных - " + MAX_REQUESTS, new ThrottlingFault());
                }

                requestCount++;
            }
        }

        return true;
    }

    @Override
    public Set<QName> getHeaders() {
        return Collections.emptySet();
    }

    @Override
    public void close(MessageContext mc) {
    }

    @Override
    public boolean handleFault(SOAPMessageContext mc) {
        return true;
    }
}
