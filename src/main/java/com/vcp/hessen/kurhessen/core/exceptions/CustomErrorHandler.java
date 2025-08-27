package com.vcp.hessen.kurhessen.core.exceptions;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.server.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;

//@Slf4j
//@Configuration
//public class CustomErrorHandler implements ErrorHandler {
//
//    @Override
//    public void error(ErrorEvent errorEvent) {
//        log.error("Something wrong happened", errorEvent.getThrowable());
//        if(UI.getCurrent() != null) {
//            UI.getCurrent().access(() -> Notification.show("An internal error has occurred." +
//                    "Please contact support."));
//        }
//    }
//}
