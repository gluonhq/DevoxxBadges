package com.devoxx.badges.service;

import com.devoxx.badges.model.Badge;
import com.devoxx.badges.views.AppViewManager;
import com.gluonhq.attach.connectivity.ConnectivityService;
import com.gluonhq.attach.settings.SettingsService;
import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.OperationMode;
import com.gluonhq.cloudlink.client.data.RemoteFunctionBuilder;
import com.gluonhq.cloudlink.client.data.RemoteFunctionObject;
import com.gluonhq.cloudlink.client.data.SyncFlag;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.GluonObservableObject;
import com.gluonhq.connect.provider.DataProvider;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import javax.annotation.PostConstruct;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Service {

    private static final Logger LOG = Logger.getLogger(Service.class.getName());
    private static final String BADGES = "badges-v1";
    
    private final ListProperty<Badge> badges = new SimpleListProperty<>(FXCollections.observableArrayList());
    
    private DataClient dataClient;

    @PostConstruct
    public void postConstruct() {
        dataClient = DataClientBuilder.create()
                .operationMode(OperationMode.LOCAL_ONLY)
                .build();
    }
    
    public void retrieveBadges() {
        GluonObservableList<Badge> gluonBadges = DataProvider.retrieveList(
                dataClient.createListDataReader(BADGES, Badge.class,
                SyncFlag.LIST_WRITE_THROUGH, SyncFlag.OBJECT_WRITE_THROUGH));
        
        gluonBadges.stateProperty().addListener((obs, ov, nv) -> {
            if (ConnectState.SUCCEEDED.equals(nv)) {
                badges.set(gluonBadges);
            }
        });
    }
    
    public Badge addBadge(Badge badge) {
        badges.get().add(badge);

        SettingsService.create().map(settingsService ->
                settingsService.retrieve(AppViewManager.SAVED_ACCOUNT_EMAIL))
                .ifPresent(emailAccount -> postBadge(badge, emailAccount));

        return badge;
    }

    public void removeBadge(Badge badge) {
        badges.get().remove(badge);
    }

    public ListProperty<Badge> badgesProperty() {
        return badges;
    }

    private void postBadge(Badge badge, String emailAddress) {
        RemoteFunctionObject fnBadge = RemoteFunctionBuilder.create("saveBadge")
                .param("0", safeStr(emailAddress))
                .param("1", safeStr(badge.getBadgeId()))
                .param("2", safeStr(badge.getFirstName()))
                .param("3", safeStr(badge.getLastName()))
                .param("4", safeStr(badge.getCompany()))
                .param("5", safeStr(badge.getEmail()))
                .param("6", safeStr(badge.getCountry()))
                .param("7", safeStr(badge.getDetails()))
                .param("8", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT))
                .object();
        GluonObservableObject<String> badgeResult = fnBadge.call(String.class);
        badgeResult.setOnFailed(e -> {
            LOG.log(Level.WARNING, "Failed to call post badge: ", e.getSource().getException());
            retryPostingBadge(badge, emailAddress);
        });
        badgeResult.setOnSucceeded(e ->
                LOG.log(Level.INFO, "Response from post badge: " + badgeResult.get()));
    }

    private void retryPostingBadge(Badge badge, String emailAddress) {
        ConnectivityService.create().ifPresent(service -> {
            if (!service.isConnected()) {
                service.connectedProperty().addListener(new InvalidationListener() {
                    @Override
                    public void invalidated(Observable observable) {
                        if (service.isConnected()) {
                            postBadge(badge, emailAddress);
                        }
                        service.connectedProperty().removeListener(this);
                    }
                });
            }
        });
    }

    private static String safeStr(String s) {
        return s == null ? "" : s.trim();
    }
}
