package de.hsh.grappa.config;

public class LmsConfig {
    private String name;
    private String id;

    //@JsonIgnore
    private String password_hash;

    @Deprecated
    // acronym of "expected internal error type always merged test feedback"
    private boolean eietamtf;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword_hash() {
        return password_hash;
    }

    public void setPassword_hash(String password_hash) {
        this.password_hash = password_hash;
    }

    @Deprecated
    public boolean getEietamtf() {
        return eietamtf;
    }

    @Deprecated
    public void setEietamtf(boolean eietamtf) {
        this.eietamtf = eietamtf;
    }

    @Override
    public String toString() {
        return "LMS{" +
            "name='" + name + '\'' +
            ", id='" + id + '\'' +
            '}';
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LmsConfig other = (LmsConfig) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
