package com.vcp.hessen.kurhessen.views;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.html.*;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;
import com.vaadin.flow.theme.lumo.LumoUtility;
import com.vcp.hessen.kurhessen.data.User;
import com.vcp.hessen.kurhessen.core.security.AuthenticatedUser;
import com.vcp.hessen.kurhessen.features.events.EventConfig;
import com.vcp.hessen.kurhessen.features.inventory.InventoryConfig;
import com.vcp.hessen.kurhessen.core.views.about.AboutView;
import com.vcp.hessen.kurhessen.features.inventory.ItemView;
import com.vcp.hessen.kurhessen.features.usermanagement.views.MyselfView;
import com.vcp.hessen.kurhessen.features.usermanagement.views.MitgliederView;
import com.vcp.hessen.kurhessen.features.events.EventView;

import java.util.Optional;

import jakarta.annotation.security.PermitAll;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.vaadin.lineawesome.LineAwesomeIcon;

/**
 * The main view is a top-level placeholder for other views.
 */
@Slf4j
@PermitAll
public class MainLayout extends AppLayout {

    private H2 viewTitle;

    private final AuthenticatedUser authenticatedUser;
    private final AccessAnnotationChecker accessChecker;

    private final EventConfig eventConfig;
    private final InventoryConfig inventoryConfig;

    public MainLayout(AuthenticatedUser authenticatedUser, AccessAnnotationChecker accessChecker, EventConfig eventConfig, InventoryConfig inventoryConfig) {
        this.authenticatedUser = authenticatedUser;
        this.accessChecker = accessChecker;
        this.eventConfig = eventConfig;
        this.inventoryConfig = inventoryConfig;

        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        viewTitle = new H2();
        viewTitle.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);

        addToNavbar(true, toggle, viewTitle);
    }

    private void addDrawerContent() {
        H1 appName = new H1("Stammesverwaltung");
        appName.addClassNames(LumoUtility.FontSize.LARGE, LumoUtility.Margin.NONE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();
        log.info("eventConfig: " + eventConfig);
        log.info("inventoryConfig: " + inventoryConfig);


        if (accessChecker.hasAccess(MyselfView.class)) {
            nav.addItem(new SideNavItem("Meine Daten", MyselfView.class, LineAwesomeIcon.USER.create()));

        }

        if (accessChecker.hasAccess(MitgliederView.class)) {
            nav.addItem(new SideNavItem("Mitglieder", MitgliederView.class, LineAwesomeIcon.ADDRESS_CARD.create()));

        }
        if (eventConfig.isEnabled()) {
            if (accessChecker.hasAccess(EventView.class)) {
                nav.addItem(
                        new SideNavItem("Veranstaltungen", EventView.class, LineAwesomeIcon.TREE_SOLID.create()));
            }
        }

        if (inventoryConfig.isEnabled()) {
            if (accessChecker.hasAccess(ItemView.class)) {
                nav.addItem(
                        new SideNavItem("Inventar", ItemView.class, LineAwesomeIcon.BOX_SOLID .create()));
            }
        }

        if (accessChecker.hasAccess(AboutView.class)) {
            nav.addItem(new SideNavItem("About", AboutView.class, LineAwesomeIcon.BOOK_OPEN_SOLID.create()));

        }

        return nav;
    }

    private String getGravatarUrl(String email) {
        if (email == null) {
            return null;
        } else {
            return "https://www.gravatar.com/avatar/" + DigestUtils.sha256Hex(email);
        }
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        Optional<User> maybeUser = authenticatedUser.get();
        if (maybeUser.isPresent()) {
            User user = maybeUser.get();

            Avatar avatar = new Avatar(user.getDisplayName());



            avatar.setImage(getGravatarUrl(user.getEmail()));

//             StreamResource resource = new StreamResource("profile-pic",
//                    () -> new ByteArrayInputStream(user.getProfilePicture()));
//            avatar.setImageResource(resource);
            avatar.setThemeName("xsmall");
            avatar.getElement().setAttribute("tabindex", "-1");

            MenuBar userMenu = new MenuBar();
            userMenu.setThemeName("tertiary-inline contrast");

            MenuItem userName = userMenu.addItem("");
            Div div = new Div();
            div.add(avatar);
            div.add(user.getDisplayName());
            div.add(new Icon("lumo", "dropdown"));
            div.getElement().getStyle().set("display", "flex");
            div.getElement().getStyle().set("align-items", "center");
            div.getElement().getStyle().set("gap", "var(--lumo-space-s)");
            userName.add(div);
            userName.getSubMenu().addItem("Sign out", e -> authenticatedUser.logout());

            layout.add(userMenu);
        } else {
            Anchor loginLink = new Anchor("login", "Sign in");
            layout.add(loginLink);
        }

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        viewTitle.setText(getCurrentPageTitle());
    }

    private String getCurrentPageTitle() {
        PageTitle title = getContent().getClass().getAnnotation(PageTitle.class);
        return title == null ? "" : title.value();
    }
}
