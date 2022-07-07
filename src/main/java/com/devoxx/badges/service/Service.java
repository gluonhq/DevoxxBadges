package com.devoxx.badges.service;

import com.devoxx.badges.model.Badge;
import com.gluonhq.cloudlink.client.data.DataClient;
import com.gluonhq.cloudlink.client.data.DataClientBuilder;
import com.gluonhq.cloudlink.client.data.OperationMode;
import com.gluonhq.cloudlink.client.data.SyncFlag;
import com.gluonhq.connect.ConnectState;
import com.gluonhq.connect.GluonObservableList;
import com.gluonhq.connect.provider.DataProvider;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;

import javax.annotation.PostConstruct;

public class Service {
    
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
        return badge;
    }

    public void removeBadge(Badge badge) {
        badges.get().remove(badge);
    }

    public ListProperty<Badge> badgesProperty() {
        return badges;
    }
    
}
