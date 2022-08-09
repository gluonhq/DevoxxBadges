package com.devoxx.badges.views;

import com.devoxx.badges.model.Badge;
import com.devoxx.badges.service.Service;
import com.gluonhq.attach.barcodescan.BarcodeScanService;
import com.gluonhq.attach.settings.SettingsService;
import com.gluonhq.attach.share.ShareService;
import com.gluonhq.attach.storage.StorageService;
import com.gluonhq.charm.glisten.application.AppManager;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.AppBar;
import com.gluonhq.charm.glisten.control.CharmListView;
import com.gluonhq.charm.glisten.control.FloatingActionButton;
import com.gluonhq.charm.glisten.control.Toast;
import com.gluonhq.charm.glisten.mvc.View;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;

import javax.inject.Inject;

public class BadgesPresenter {

    private static final Logger LOG = Logger.getLogger(BadgesPresenter.class.getName());

    @FXML
    private View badgesView;

    @FXML
    private CharmListView<Badge, LocalDate> badgesListView;

    @FXML
    private ResourceBundle resources;

    @Inject
    private Service service;

    private MenuItem signUpMenuItem;
    private MenuItem signOutMenuItem;

    public void initialize() {
        FloatingActionButton scan = new FloatingActionButton();
        scan.getStyleClass().add("badge-scanner");
        scan.showOn(badgesView);

        scan.setOnAction(e -> {
            BarcodeScanService.create().ifPresentOrElse(s -> {
                final Optional<String> scanQr = s.scan(resources.getString("BADGES.QR.TITLE"), null, null);
                scanQr.ifPresent(this::addBadge);
            },
            () -> addBadge(getDummyQR()));
        });

        badgesListView.setCellFactory(p -> new BadgeCell());
        badgesListView.setHeaderCellFactory(p -> new HeaderCell());
        badgesListView.setHeadersFunction(badge -> badge.getLocalDateTime().toLocalDate());
        badgesListView.setPlaceholder(new Label(resources.getString("BADGES.EMPTY")));

        service.badgesProperty().addListener((ListChangeListener.Change<? extends Badge> c) -> {
            ObservableList<Badge> badges = FXCollections.observableArrayList(new ArrayList<Badge>(c.getList()));
            badgesListView.setItems(badges);
            badgesListView.setComparator(Comparator.comparing(Badge::getDateTime));
        });

        signUpMenuItem = getSignUpMenuItem();
        signOutMenuItem = getSignOutMenuItem();
        badgesView.showingProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue) {

                String emailAccount = SettingsService.create().map(settingsService ->
                                settingsService.retrieve(AppViewManager.SAVED_ACCOUNT_EMAIL))
                        .orElse(null);
                AppManager appManager = AppManager.getInstance();
                AppBar appBar = appManager.getAppBar();
                final Button shareButton = getShareButton();
                shareButton.disableProperty().bind(badgesListView.itemsProperty().emptyProperty());
                appBar.setNavIcon(MaterialDesignIcon.MENU.button(e ->
                        AppManager.getInstance().getDrawer().open()));
                appBar.setTitleText(resources.getString("BADGES.VIEW"));
                appBar.getActionItems().setAll(shareButton);
                appBar.getMenuItems().setAll(emailAccount == null || emailAccount.isEmpty() ? signUpMenuItem : signOutMenuItem);
            }
        });

        service.retrieveBadges();
    }

    private void addBadge(String qr) {
        Badge badge = new Badge(qr);
        if (badge.getBadgeId() != null) {
            service.badgesProperty().get().stream()
                    .filter(b -> b != null && b.getBadgeId() != null && b.getBadgeId().equals(badge.getBadgeId()))
                    .findAny()
                    .ifPresentOrElse(b -> AppViewManager.EDITION_VIEW.switchView(ViewStackPolicy.USE)
                            .ifPresent(presenter -> ((EditionPresenter) presenter).setBadge(b, false)),
                        () -> {
                            badge.setDateTime(System.currentTimeMillis());
                            service.addBadge(badge);
                            AppViewManager.EDITION_VIEW.switchView(ViewStackPolicy.USE)
                                    .ifPresent(presenter -> ((EditionPresenter) presenter).setBadge(badge, true));
                        });

        } else {
            Toast toast = new Toast(resources.getString("BADGES.BAD.QR"));
            toast.show();
        }
    }

    private MenuItem getSignUpMenuItem() {
        MenuItem item = new MenuItem();
        item.setText(resources.getString("ACTIVATION.MENU.IN"));
        item.setOnAction(e -> AppViewManager.SIGN_UP_VIEW.switchView(ViewStackPolicy.USE));
        return item;
    }

    private MenuItem getSignOutMenuItem() {
        MenuItem item = new MenuItem();
        item.setText(resources.getString("ACTIVATION.MENU.OUT"));
        item.setOnAction(e -> SettingsService.create().ifPresent(settingsService -> {
            settingsService.remove(AppViewManager.SAVED_ACCOUNT_EMAIL);
            Toast toast = new Toast(resources.getString("ACTIVATION.SIGNED.OUT"));
            toast.show();
            AppManager.getInstance().getAppBar().getMenuItems().setAll(signUpMenuItem);
        }));
        return item;
    }

    private Button getShareButton() {
        return MaterialDesignIcon.SHARE.button(e -> {
            File root = StorageService.create()
                    .flatMap(storage -> storage.getPublicStorage("Documents"))
                    .orElse(null);
            if (root != null) {
                if (!root.exists()) {
                    root.mkdirs();
                }
                File file = new File(root, "Devoxx-badges.csv");
                if (file.exists()) {
                    file.delete();
                }
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                    writer.write("ID,First Name,Last Name,Company,Email,Country,Details,Timestamp");
                    writer.newLine();
                    for (Badge badge : service.badgesProperty().get()) {
                        writer.write(badge.toCSV());
                        writer.newLine();
                    }
                } catch (IOException ex) {
                    LOG.log(Level.WARNING, "Error writing csv file ", ex);
                }
                ShareService.create().ifPresentOrElse(s -> {
                    s.share(resources.getString("BADGES.SHARE.SUBJECT"),
                            MessageFormat.format(resources.getString("BADGES.SHARE.MESSAGE"), DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).format(LocalDate.now())),
                            "text/plain", file);
                }, () -> LOG.log(Level.INFO, "Shared file: " + file));
            } else {
                LOG.log(Level.WARNING, "Error accessing local storage");
            }

        });
    }

    private static String getDummyQR() {
        List<String> qrs = Arrays.asList(
                "1::Smith::John::Devoxx::john.smith@devoxx.com::USA",
                "2::Doe::Stacey::Devoxx::stacey.doe@devoxx.com::Belgium",
                "3::Gates::Paul::Devoxx::paul.gates@devoxx.com::India",
                "4::Bisl::Elon::Devoxx::elon.bisl@devoxx.com::Spain"
        );
        return qrs.get(new Random().nextInt(qrs.size()));
    }
}
