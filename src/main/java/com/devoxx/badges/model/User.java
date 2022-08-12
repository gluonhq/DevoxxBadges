package com.devoxx.badges.model;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {

    // signedUpProperty
    private final ReadOnlyBooleanWrapper signedUp = new ReadOnlyBooleanWrapper(this, "signedUp");
    public final ReadOnlyBooleanProperty signedUpProperty() {
       return signedUp.getReadOnlyProperty();
    }
    public final boolean isSignedUp() {
       return signedUp.get();
    }

    // emailProperty
    private final StringProperty email = new SimpleStringProperty(this, "email") {
        @Override
        protected void invalidated() {
            signedUp.setValue(get() != null && !get().isEmpty());
        }
    };
    public final StringProperty emailProperty() {
       return email;
    }
    public final String getEmail() {
       return email.get();
    }
    public final void setEmail(String value) {
        email.set(value);
    }

}
