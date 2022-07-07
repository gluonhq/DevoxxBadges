package com.devoxx.badges.views;

import com.gluonhq.charm.glisten.visual.MaterialDesignIcon;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class Placeholder extends VBox {

    public Placeholder(String titleText, String messageText, MaterialDesignIcon image) {
        setAlignment(Pos.TOP_CENTER);

        Label message = new Label();
        message.setWrapText(true);
        message.getStyleClass().add("message");

        getStyleClass().add("placeholder");
        getChildren().add(getNodeFromIcon(image));

        if (titleText != null && !titleText.isEmpty()) {
            Label title = new Label(titleText);
            title.getStyleClass().add("title");
            getChildren().add(title);
        }

        message.setText(messageText);
        getChildren().add(message);
    }

    private Node getNodeFromIcon(MaterialDesignIcon icon) {
        Node graphic = icon.graphic();
        graphic.getStyleClass().add("placeholder-graphic");
        return graphic;
    }
}

