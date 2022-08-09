package com.devoxx.badges.views;

import com.devoxx.badges.model.Badge;
import com.devoxx.badges.service.Service;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.Dialog;
import com.gluonhq.charm.glisten.mvc.View;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.inject.Inject;
import java.util.Objects;
import java.util.ResourceBundle;

public class EditionPresenter {

    @FXML
    private View editionView;

    @Inject
    private Service service;

    @FXML
    private TextField firstName;

    @FXML
    private TextField lastName;

    @FXML
    private TextField company;

    @FXML
    private TextField email;

    @FXML
    private TextField country;

    @FXML
    private TextArea details;

    @FXML
    private ResourceBundle resources;

    private Badge badge;
    private boolean textChanged;
    private boolean scanned;
    private final ChangeListener<String> detailsChangeListener = (observable, oldValue, newValue) -> {
        if (newValue != null && !newValue.isEmpty()) {
            textChanged = true;
        }
    };

    public void initialize() {
        editionView.setOnShowing(event -> {
            AppManager appManager = AppManager.getInstance();
            AppBar appBar = appManager.getAppBar();
            appBar.setNavIcon(MaterialDesignIcon.ARROW_BACK.button(e ->
                    appManager.switchToPreviousView()));
            appBar.setTitleText(AppViewManager.EDITION_VIEW.getTitle());
            appBar.getActionItems().add(MaterialDesignIcon.DELETE.button(e -> {
                final Dialog<Button> dialog = createDialog();
                dialog.showAndWait();
            }));
        });

        editionView.setOnHiding(event -> {
            if (badge != null && (scanned || textChanged)) {
                saveBadge();
            }
            textChanged = false;
            details.textProperty().removeListener(detailsChangeListener);
        });

        editionView.setOnShown(event -> editionView.requestFocus());
    }

    public void setBadge(Badge badge, boolean scanned) {
        Objects.requireNonNull(badge);
        Objects.requireNonNull(badge.getBadgeId());

        this.badge = badge;
        this.scanned = scanned;

        firstName.setText(badge.getFirstName());
        lastName.setText(badge.getLastName());
        company.setText(badge.getCompany());
        email.setText(badge.getEmail());
        country.setText(badge.getCountry());
        details.setText(badge.getDetails());

        details.textProperty().addListener(detailsChangeListener);
    }

    private Dialog<Button> createDialog() {
        Dialog<Button> dialog = new Dialog<>();
        Placeholder deleteDialogContent = new Placeholder(resources.getString("BADGE.DIALOG.REMOVE.TITLE"),
                resources.getString("BADGE.DIALOG.REMOVE.CONTENT"), MaterialDesignIcon.HELP);
        deleteDialogContent.setPrefWidth(AppManager.getInstance().getView().getScene().getWidth() - 40);

        dialog.setContent(deleteDialogContent);
        Button yesButton = new Button(resources.getString("DIALOG.YES"));
        Button noButton = new Button(resources.getString("DIALOG.NO"));
        yesButton.setOnAction(ev -> {
            service.removeBadge(badge);
            badge = null;
            dialog.hide();
            AppManager.getInstance().switchToPreviousView();
        });
        noButton.setOnAction(ev -> dialog.hide());
        dialog.getButtons().addAll(noButton, yesButton);
        return dialog;
    }

    private void saveBadge() {
        service.removeBadge(badge);

        badge.setFirstName(firstName.getText());
        badge.setLastName(lastName.getText());
        badge.setCompany(company.getText());
        badge.setEmail(email.getText());
        badge.setCountry(country.getText());
        badge.setDetails(details.getText());

        service.addBadge(badge);
    }

}
