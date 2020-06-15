package de.hsh.grappa.service;

import java.util.Properties;

public interface GradingEnvironmentSetup {
    void init(Properties settings);

    void setup() throws Exception;

    void teardown() throws Exception;
}
