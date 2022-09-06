package com.devoxx.badges.views;

import com.devoxx.badges.model.User;
import com.devoxx.badges.service.Service;
import com.gluonhq.attach.settings.SettingsService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Toast;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import javax.inject.Inject;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import java.util.logging.Logger;

public class SyncPresenter {

    private static final Logger LOG = Logger.getLogger(SyncPresenter.class.getName());

    @FXML
    private View syncView;

    @FXML
    private Label description;

    @FXML
    private Button export;

    @FXML
    private Button signout;

    @Inject
    private Service service;

    @Inject
    private User user;

    @FXML
    private ResourceBundle resources;

    public void initialize() {
        syncView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppManager appManager = AppManager.getInstance();
                AppBar appBar = appManager.getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText(resources.getString("SYNC.VIEW"));
            }
        });

        description.setText(MessageFormat.format(resources.getString("SYNC.DESCRIPTION"), user.getEmail()));

        export.setOnAction(e -> {
            service.exportRemoteBadges();
            Toast toast = new Toast(resources.getString("SYNC.EXPORTED"));
            toast.show();
            AppManager.getInstance().goHome();
        });

        signout.setOnAction(e -> {
            SettingsService.create().ifPresent(settingsService -> {
                user.setEmail(null);
                settingsService.remove(AppViewManager.SAVED_ACCOUNT_EMAIL);
                Toast toast = new Toast(resources.getString("SYNC.SIGNED.OUT"));
                toast.show();
            });
            AppManager.getInstance().goHome();
        });
    }
}
