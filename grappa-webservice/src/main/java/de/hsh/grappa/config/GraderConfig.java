package de.hsh.grappa.config;

public class GraderConfig {
  private String id;
  private String name;
  private boolean enabled;
  private String class_path;
  private String file_extension;
  private String class_name;
  private String config_path;
  private int timeout_seconds = 120;
  private int concurrent_grading_processes = 5;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public boolean getEnabled() { return enabled; }

  public void setEnabled(boolean enabled) { this.enabled = enabled; }

  public String getClass_path() {
    return class_path;
  }

  public void setClass_path(String class_path) {
    this.class_path = class_path;
  }

  public String getFile_extension() {
    return file_extension;
  }

  public void setFile_extension(String file_extension) {
    this.file_extension = file_extension;
  }

  public String getClass_name() {
    return class_name;
  }

  public void setClass_name(String class_name) {
    this.class_name = class_name;
  }

  public String getConfig_path() {
    return config_path;
  }

  public void setConfig_path(String config_path) {
    this.config_path = config_path;
  }

  public int getTimeout_seconds() {
    return timeout_seconds;
  }

  public void setTimeout_seconds(int timeout_seconds) {
    this.timeout_seconds = timeout_seconds;
  }

  public int getConcurrent_grading_processes() {
    return concurrent_grading_processes;
  }

  public void setConcurrent_grading_processes(int concurrent_grading_processes) {
    this.concurrent_grading_processes = concurrent_grading_processes;
  }

  @Override
  public String toString() {
    return "Grader{" +
            "id='" + id + '\'' +
            ", name=" + name + '\'' +
            ", enabled='" + enabled + '\'' +
            ", class_path=" + class_path + '\'' +
            ", file_extension=" + file_extension + '\'' +
            ", class_name='" + class_name + '\'' +
            ", config_path='" + config_path + '\'' +
            ", max_runtime_seconds=" + timeout_seconds +
            ", max_concurrent_grade_processes=" + concurrent_grading_processes +
            '}';
  }
}
