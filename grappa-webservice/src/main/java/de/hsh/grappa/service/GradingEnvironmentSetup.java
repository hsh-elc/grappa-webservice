package de.hsh.grappa.service;

import java.util.Properties;

/**
 * This interface is for setting up grading environments,
 * such as Vagrant.
 *
 * The idea is to run the entire Grappa web service within
 * an isolated and secure environment.
 */
public interface GradingEnvironmentSetup {
    void init(Properties settings);

    void setup() throws Exception;

    void teardown() throws Exception;
}
