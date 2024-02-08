package dev.revivalo.playerwarps.utils;

import dev.revivalo.playerwarps.configuration.enums.Lang;
import dev.revivalo.playerwarps.warp.Warp;

import java.util.Comparator;
import java.util.Locale;

public final class SortingUtils {
    public enum SortType {
        VISITS(Comparator.comparing(Warp::getVisits).reversed()),
        LATEST(Comparator.comparing(Warp::getDateCreated).reversed()),
        RATING(Comparator.comparing(Warp::getRating).reversed());

        private final Comparator<Warp> comparator;

        SortType(Comparator<Warp> comparator) {
            this.comparator = comparator;
        }

        public String getName() {
            return Lang.valueOf(name().toUpperCase(Locale.ENGLISH)).asColoredString();
        }

        public Comparator<Warp> getComparator() {
            return comparator;
        }
    }


}
