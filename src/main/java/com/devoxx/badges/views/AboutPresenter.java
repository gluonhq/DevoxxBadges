package com.devoxx.badges.views;

import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.fxml.FXML;

import java.util.ResourceBundle;
import java.util.logging.Logger;

public class AboutPresenter {

    private static final Logger LOG = Logger.getLogger(AboutPresenter.class.getName());

    @FXML
    private View aboutView;

    @FXML
    private ResourceBundle resources;

    public void initialize() {
        FloatingActionButton start = new FloatingActionButton();
        start.setText(MaterialDesignIcon.PLAY_CIRCLE_OUTLINE.text);
        start.setOnAction(e -> AppViewManager.BADGES_VIEW.switchView());
        start.showOn(aboutView);

        aboutView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {
                AppManager appManager = AppManager.getInstance();
                AppBar appBar = appManager.getAppBar();
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText(resources.getString("MAIN.VIEW"));
            }
        });
    }
}
