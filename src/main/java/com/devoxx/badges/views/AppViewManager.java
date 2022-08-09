package com.devoxx.badges.views;

import com.gluonhq.charm.glisten.afterburner.AppView;
import com.gluonhq.charm.glisten.afterburner.AppViewRegistry;
import com.gluonhq.charm.glisten.afterburner.Utils;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.Avatar;
import com.gluonhq.charm.glisten.control.NavigationDrawer;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.scene.image.Image;

import java.util.Locale;
import java.util.ResourceBundle;

import static com.gluonhq.charm.glisten.afterburner.AppView.Flag.HOME_VIEW;
import static com.gluonhq.charm.glisten.afterburner.AppView.Flag.SHOW_IN_DRAWER;
import static com.gluonhq.charm.glisten.afterburner.AppView.Flag.SKIP_VIEW_STACK;

public class AppViewManager {

    public static final AppViewRegistry REGISTRY = new AppViewRegistry();
    public static final String SAVED_ACCOUNT_EMAIL = "devoxx_badges_account_email";

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.devoxx.badges.views.drawer");

    public static final AppView MAIN_VIEW = view("Intro", MainPresenter.class, MaterialDesignIcon.HOME, SHOW_IN_DRAWER, HOME_VIEW, SKIP_VIEW_STACK);
    public static final AppView BADGES_VIEW = view("Badges", BadgesPresenter.class, MaterialDesignIcon.SCANNER, SHOW_IN_DRAWER);
    public static final AppView EDITION_VIEW = view("Badge Edition", EditionPresenter.class, MaterialDesignIcon.CONTACTS);
    public static final AppView SIGN_UP_VIEW = view("Sign up", ActivatePresenter.class, MaterialDesignIcon.LOCK);

    private static AppView view(String title, Class<?> presenterClass, MaterialDesignIcon menuIcon, AppView.Flag... flags ) {
        return REGISTRY.createView(name(presenterClass), title, presenterClass, menuIcon, flags);
    }

    private static String name(Class<?> presenterClass) {
        return presenterClass.getSimpleName().toUpperCase(Locale.ROOT).replace("PRESENTER", "");
    }
    
    public static void registerViewsAndDrawer() {
        REGISTRY.getViews().forEach(AppView::registerView);

        NavigationDrawer.Header header = new NavigationDrawer.Header(bundle.getString("drawer.header.title"),
                bundle.getString("drawer.header.description"),
                new Avatar(21, new Image(AppViewManager.class.getResourceAsStream("/icon.png"))));

        Utils.buildDrawer(AppManager.getInstance().getDrawer(), header, REGISTRY.getViews());
    }
}
