package ar.edu.itba.pod.client;

public final class Queries {
    private Queries() {
        // static
    }

    public static final String IN_DELIM = ";";

    public static final String COLLECTIONS_PREFIX = "g16-";
    public static String hazelcastNamespace(final String name) {
        return COLLECTIONS_PREFIX + name;
    }
}
