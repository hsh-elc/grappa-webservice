package de.hsh.grappa.config;

public class RedisConfig {
    private String host;
    private int port;
    //@JsonIgnore
    private String password;

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "Redis{" +
            "host='" + host + '\'' +
            ", port=" + port +
            '}';
    }
}
