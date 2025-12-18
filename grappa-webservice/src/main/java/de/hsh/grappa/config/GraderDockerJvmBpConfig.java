package de.hsh.grappa.config;

public class GraderDockerJvmBpConfig {
    private String image_name;

    private String run_container_in_docker_network;

    private String copy_submission_to_dir_path;
    private String load_response_from_dir_path;
    private String copy_grader_plugin_defaults_properties_to_file;

    public String getImage_name() {
        return image_name;
    }

    public void setImage_name(String image_name) {
        this.image_name = image_name;
    }

    public String getRun_container_in_docker_network() {
        return run_container_in_docker_network;
    }

    public void setRun_container_in_docker_network(String run_container_in_docker_network) {
        this.run_container_in_docker_network = run_container_in_docker_network;
    }

    public String getCopy_submission_to_dir_path() {
        return copy_submission_to_dir_path;
    }

    public void setCopy_submission_to_dir_path(String copy_submission_to_dir_path) {
        this.copy_submission_to_dir_path = copy_submission_to_dir_path;
    }

    public String getLoad_response_from_dir_path() {
        return load_response_from_dir_path;
    }

    public void setLoad_response_from_dir_path(String load_response_from_dir_path) {
        this.load_response_from_dir_path = load_response_from_dir_path;
    }

    public String getCopy_grader_plugin_defaults_properties_to_file() {
        return copy_grader_plugin_defaults_properties_to_file;
    }

    public void setCopy_grader_plugin_defaults_properties_to_file(String copy_grader_plugin_defaults_properties_to_file) {
        this.copy_grader_plugin_defaults_properties_to_file = copy_grader_plugin_defaults_properties_to_file;
    }

    @Override
    public String toString() {
        return "GraderDockerJvmBpConfig{"
            + "image_name=" + image_name
            + ", run_container_in_docker_network=" + run_container_in_docker_network
            + ", copy_submission_to_dir_path=" + copy_submission_to_dir_path
            + ", load_response_from_dir_path=" + load_response_from_dir_path
            + ", copy_grader_plugin_defaults_properties_to_file=" + copy_grader_plugin_defaults_properties_to_file
            + "}";
    }
}
