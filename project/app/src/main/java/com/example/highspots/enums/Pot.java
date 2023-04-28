package com.example.highspots.enums;

import androidx.annotation.NonNull;

public enum Pot {

    // Pots here
    not_a_pot(null);

    final private String pot;

    Pot(String pot) {
        this.pot = pot;
    }

    @NonNull
    @Override
    public String toString() {
        return this.pot;
    }
}
