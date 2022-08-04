package de.hsh.grappa;

import de.hsh.grappa.service.GradingEnvironmentSetup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Properties;

/**
 * Sets up a vagrant host environment for grappa to run in.
 */
public class VagrantHostSetup implements GradingEnvironmentSetup {
    private static final Logger log = LoggerFactory.getLogger(VagrantHostSetup.class);

    //private String vagrantBinPath = "/opt/vagrant/bin";
    //private String shell = "/bin/bash";
    private String shell = "cmd.exe";
    private String vagrantFileDirectoryPath;

    public void init(Properties props) {
        vagrantFileDirectoryPath = props.getProperty("vagrantfile_directory_path");
        shell = props.getProperty("shell");
    }

    public void setup() throws Exception {
        log.info("Spinning up vagrant host. This may take a few minutes...");
        //envs.put("PATH", vagrantBinPath);
        //String[] up = {shell, "-c", "vagrant up"}; //linux
        String[] up = {shell, "/C", "vagrant up"};
        int exitCode = runProcess(up);
        log.info("Container host startup finished with exit code: {}", exitCode);
        if (exitCode != 0)
            throw new Exception("Container host failed to start.");
    }

    public void teardown() throws Exception {
        log.debug("VagrantHostSetup.teardown()");
        //String[] halt = {shell, "-c", "vagrant halt"}; //linux
        String[] halt = {shell, "/C", "vagrant halt"};
        runProcess(halt);
    }

    private void readProcessOutputToLog(Process proc) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(proc.getInputStream()));) {
            String line = null;
            while ((line = reader.readLine()) != null) {
                log.info(line);
            }
        }
    }

    private int runProcess(String[] cmds) throws Exception {
        ProcessBuilder builder = new ProcessBuilder(cmds).redirectErrorStream(true);
        builder = builder.directory(new File(vagrantFileDirectoryPath));
        Map<String, String> envs = builder.environment();
        Process pr = builder.start();
        readProcessOutputToLog(pr);
        pr.waitFor();
        return pr.exitValue();
    }
}
