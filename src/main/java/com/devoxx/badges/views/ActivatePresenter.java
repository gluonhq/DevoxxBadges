package com.devoxx.badges.views;

import com.gluonhq.attach.settings.SettingsService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.TextField;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.util.regex.Pattern;

public class ActivatePresenter {

    private static final Pattern emailPattern = Pattern
            .compile("[a-zA-Z0-9[!#$%&'()*+,/\\-_\\.\"]]+@[a-zA-Z0-9[!#$%&'()*+,/\\-_\"]]+\\.[a-zA-Z0-9[!#$%&'()*+,/\\-_\"\\.]]+");

    @FXML
    private View activateView;
    @FXML
    private TextField username;
    @FXML
    private Button submit;

    public void initialize() {
        activateView.setOnShowing(event -> {
            AppBar appBar = AppManager.getInstance().getAppBar();
            appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e ->
                    AppManager.getInstance().switchToPreviousView()));
            appBar.setTitleText(AppViewManager.SIGN_UP_VIEW.getTitle());
            username.setText(null);
        });

        submit.disableProperty().bind(Bindings.createBooleanBinding(() -> !isValidEmail(username.getText()), username.textProperty()));
    }

    @FXML
    private void submit() {
        SettingsService.create().ifPresent(settingsService ->
                settingsService.store(AppViewManager.SAVED_ACCOUNT_EMAIL, username.getText()));
        AppManager.getInstance().switchToPreviousView();
    }

    public static boolean isValidEmail(String email) {
        if (email == null || email.isEmpty()) {
            return false;
        }
        return emailPattern.matcher(email).matches();
    }
}
