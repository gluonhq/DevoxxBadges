package com.devoxx.badges;

import com.airhacks.afterburner.injection.Injector;
import com.devoxx.badges.model.User;
import com.devoxx.badges.views.AppViewManager;
import com.gluonhq.attach.settings.SettingsService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.visual.Swatch;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class DevoxxBadges extends Application {

    private final AppManager appManager = AppManager.initialize(this::postInit);

    @Override
    public void init() {
        AppViewManager.registerViewsAndDrawer();
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        appManager.start(primaryStage);
    }

    private void postInit(Scene scene) {
        Swatch.ORANGE.assignTo(scene);

        scene.getStylesheets().add(DevoxxBadges.class.getResource("style.css").toExternalForm());
        ((Stage) scene.getWindow()).getIcons().add(new Image(DevoxxBadges.class.getResourceAsStream("/icon.png")));

        User user = Injector.instantiateModelOrService(User.class);
        SettingsService.create().map(settingsService ->
                        settingsService.retrieve(AppViewManager.SAVED_ACCOUNT_EMAIL))
                .ifPresentOrElse(user::setEmail, () -> user.setEmail(null));
    }

    public static void main(String args[]) {
        launch(args);
    }
}
