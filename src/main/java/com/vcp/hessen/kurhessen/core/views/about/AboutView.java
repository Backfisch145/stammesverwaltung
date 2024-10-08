package com.vcp.hessen.kurhessen.core.views.about;

import com.vaadin.flow.component.Composite;
import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.auth.AnonymousAllowed;
import com.vcp.hessen.kurhessen.views.MainLayout;

@PageTitle("About")
@Route(value = "about", layout = MainLayout.class)
@AnonymousAllowed
@Uses(Icon.class)
public class AboutView extends Composite<VerticalLayout> {

    public AboutView() {
        getContent().setWidth("100%");
        getContent().getStyle().set("flex-grow", "1");
    }
}
