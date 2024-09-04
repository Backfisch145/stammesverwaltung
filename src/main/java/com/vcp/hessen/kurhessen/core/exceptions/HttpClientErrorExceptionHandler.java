package com.vcp.hessen.kurhessen.core.exceptions;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.router.*;
import com.vcp.hessen.kurhessen.views.MainLayout;
import org.springframework.web.client.HttpClientErrorException;

@Tag(Tag.DIV)
@ParentLayout(MainLayout.class)
public class HttpClientErrorExceptionHandler extends Component implements HasErrorParameter<HttpClientErrorException> {
    @Override
    public int setErrorParameter(BeforeEnterEvent event,
                                 ErrorParameter<HttpClientErrorException>
                                         parameter) {


        getElement().setText(parameter.getException().getStatusText());
        return parameter.getException().getStatusCode().value();
    }
}
