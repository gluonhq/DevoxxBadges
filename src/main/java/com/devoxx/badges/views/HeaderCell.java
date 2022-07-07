package com.devoxx.badges.views;

import com.devoxx.badges.model.Badge;
import com.gluonhq.charm.glisten.control.CharmListCell;
import javafx.scene.control.Label;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class HeaderCell extends CharmListCell<Badge> {

    private final Label label;
    private Badge currentItem;
    private final DateTimeFormatter dateFormat;
    
    HeaderCell() {
        label = new Label();
        dateFormat = DateTimeFormatter.ofPattern("EEEE, MMM dd", Locale.ENGLISH);
    }

    @Override
    public void updateItem(Badge item, boolean empty) {
        super.updateItem(item, empty);
        currentItem = item;
        if (!empty && item != null) {
            updateWithSettings();
            setGraphic(label);
        } else {
            setGraphic(null);
        }
    }

    private void updateWithSettings() {
        if (currentItem != null) {
            label.setText(dateFormat.format(currentItem.getLocalDateTime()));
        } else {
            label.setText("");
        }
    }
    
}
