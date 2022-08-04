package de.hsh.grappa.config;

/**
 * represents a graderID, a graderID
 * A grader is identified using the grader name and the grader version
 */
public class GraderID {
    private String name;
    private String version;

    public static final String REDIS_SEPARATOR = "$";

    public GraderID() {
    } //used so the yaml mapper can generate the GraderID object automatically with the config file

    public GraderID(String graderName, String graderVersion) {
        this.name = graderName;
        this.version = graderVersion;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String toConfigString() {
        return "GraderID{" +
            "name='" + name + '\'' +
            ", version='" + version + '\'' +
            "}";
    }

    /**
     * @return the graderID as a string (the graderName with the graderVersion in parentheses)
     */
    @Override
    public String toString() {
        return name + "(" + version + ")";
    }

    /**
     * this method is only used to save the GraderID in the redis cache
     *
     * @return the GraderID as a String (graderName and graderVersion seperated by "$")
     */
    public String toRedisString() {
        return name + "$" + version;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof GraderID) {
            GraderID other = (GraderID) o;
            return this.name.equals(other.name) && this.version.equals(other.version);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, version);
    }
}
