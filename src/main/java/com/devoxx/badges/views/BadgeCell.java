package com.devoxx.badges.views;

import com.devoxx.badges.model.Badge;
import com.gluonhq.charm.glisten.application.ViewStackPolicy;
import com.gluonhq.charm.glisten.control.CharmListCell;
import com.gluonhq.charm.glisten.control.ListTile;
import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;

public class BadgeCell extends CharmListCell<Badge> {

    private static final int MAX_TEXT_SIZE = 100;

    protected final ListTile tile;

    public BadgeCell() {
        tile = new ListTile();
        tile.setPrimaryGraphic(MaterialDesignIcon.CONTACTS.graphic());
        tile.setSecondaryGraphic(MaterialDesignIcon.CHEVRON_RIGHT.graphic());
        setText(null);
        getStyleClass().add("badge-cell");
    }

    @Override public void updateItem(Badge badge, boolean empty) {
        super.updateItem(badge, empty);

        if (badge != null && !empty) {
            tile.textProperty().setAll(badge.getFirstName() + " " + badge.getLastName(), 
                        badge.getCompany() + " - " + badge.getEmail(), 
                        truncateText(badge.getDetails()));
            setGraphic(tile);

            tile.setOnMouseReleased(event -> {
                AppViewManager.EDITION_VIEW.switchView(ViewStackPolicy.USE).ifPresent(presenter ->
                        ((EditionPresenter) presenter).setBadge(badge, false));
            });
        } else {
            setGraphic(null);
        }
    }

    private String truncateText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        String truncatedText = text;
        if (text.length() > MAX_TEXT_SIZE) {
            truncatedText = text.substring(0, MAX_TEXT_SIZE - 1) + "...";
        }
        return truncatedText;
    }
}
