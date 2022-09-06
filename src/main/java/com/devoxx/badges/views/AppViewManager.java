package com.devoxx.badges.views;

import com.airhacks.afterburner.injection.Injector;
import com.devoxx.badges.model.User;
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
    public static final String FIRST_RUN = "devoxx_badges_first_run";

    private static final ResourceBundle bundle = ResourceBundle.getBundle("com.devoxx.badges.views.drawer");

    public static final AppView BADGES_VIEW = view(bundle.getString("BADGES.VIEW"), BadgesPresenter.class, MaterialDesignIcon.SCANNER, SHOW_IN_DRAWER, HOME_VIEW);
    public static final AppView EDITION_VIEW = view(bundle.getString("EDITION.VIEW"), EditionPresenter.class, MaterialDesignIcon.CONTACTS);
    public static final AppView SIGN_UP_VIEW = view(bundle.getString("ACTIVATION.VIEW"), ActivatePresenter.class, MaterialDesignIcon.LOCK, SHOW_IN_DRAWER);
    public static final AppView SYNC_VIEW = view(bundle.getString("SYNC.VIEW"), SyncPresenter.class, MaterialDesignIcon.LOCK, SHOW_IN_DRAWER);
    public static final AppView ABOUT_VIEW = view(bundle.getString("ABOUT.VIEW"), AboutPresenter.class, MaterialDesignIcon.INFO, SHOW_IN_DRAWER, SKIP_VIEW_STACK);

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

        NavigationDrawer drawer = AppManager.getInstance().getDrawer();
        Utils.buildDrawer(drawer, header, REGISTRY.getViews());

        User user = Injector.instantiateModelOrService(User.class);
        NavigationDrawer.Item signupItem = drawer.getItems().stream()
                .filter(item -> ((NavigationDrawer.Item) item).getTitle().equals(SIGN_UP_VIEW.getTitle()))
                .map(NavigationDrawer.Item.class::cast)
                .findFirst()
                .orElseThrow();
        signupItem.visibleProperty().bind(user.signedUpProperty().not());
        signupItem.managedProperty().bind(signupItem.visibleProperty());

        NavigationDrawer.Item syncItem = drawer.getItems().stream()
                .filter(item -> ((NavigationDrawer.Item) item).getTitle().equals(SYNC_VIEW.getTitle()))
                .map(NavigationDrawer.Item.class::cast)
                .findFirst()
                .orElseThrow();
        syncItem.visibleProperty().bind(signupItem.visibleProperty().not());
        syncItem.managedProperty().bind(syncItem.visibleProperty());
    }
}
