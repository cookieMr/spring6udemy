package mr.cookie.spring6udemy.model.entities;

public final class Constant {

    /**
     * A strategy generator name for JPA Entities.
     */
    public static final String UUID_NAME = "UUID";

    /**
     * A strategy generator fully qualified class name for JPA Entities.
     */
    public static final String UUID_GENERATOR_STRATEGY = "org.hibernate.id.UUIDGenerator";

    private Constant() {
        throw new IllegalCallerException();
    }

}
