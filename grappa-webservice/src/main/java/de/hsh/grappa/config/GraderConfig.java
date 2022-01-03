package de.hsh.grappa.config;

import java.util.Properties;

public class GraderConfig {
  private String id;
  private String name;
  private boolean enabled;
  private int timeout_seconds = 120;
  private int concurrent_grading_processes = 5;
  private String logging_level;
  
//  private String class_path;
//  private String file_extension;
//  private String class_name;
//  private String config_path;
  
  private String subdir;
  private String backend_plugin_classname;
  private String relative_classpathes="";
  private String fileextensions=".jar";
  
  private Properties grader_plugin_defaults;
  
  private String operating_mode;
  private GraderDockerJvmBpConfig docker_jvm_bp;
  private GraderHostJvmBpConfig host_jvm_bp;

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
  
  public String getLogging_level() {
	return logging_level;
  }

  public void setLogging_level(String logging_level) {
	this.logging_level = logging_level;
  }
  
  public String getSubdir(){
    return subdir;
  }
  
  public void setSubdir(String subdir){
    this.subdir=subdir;
  }
  
  public String getBackend_plugin_classname(){
    return backend_plugin_classname;
  }
  
  public void setBackend_plugin_classname(String backend_plugin_classname){
    this.backend_plugin_classname=backend_plugin_classname;
  }
  
  public String getRelative_classpathes(){
    return relative_classpathes;
  }
  
  public void setRelative_classpathes(String relative_classpathes){
    this.relative_classpathes=relative_classpathes;
  }
  
  public String getFileextensions(){
    return fileextensions;
  }
  
  public void setFileextensions(String fileextensions){
    this.fileextensions=fileextensions;
  }
  
  public Properties getGrader_plugin_defaults(){
    return grader_plugin_defaults;
  }
  
  public void setGrader_plugin_defaults(Properties grader_plugin_defaults){
    this.grader_plugin_defaults=grader_plugin_defaults;
  }
  
  public String getOperating_mode(){
    return operating_mode;
  }
  
  public void setOperating_mode(String operating_mode){
    this.operating_mode=operating_mode;
  }

  public GraderDockerJvmBpConfig getDocker_jvm_bp(){
    return docker_jvm_bp;
  }
  
  public void setDocker_jvm_bp(GraderDockerJvmBpConfig docker_jvm_bp){
    this.docker_jvm_bp=docker_jvm_bp;
  }
  
  public GraderHostJvmBpConfig getHost_jvm_bp(){
    return host_jvm_bp;
  }
  
  public void setHost_jvm_bp(GraderHostJvmBpConfig host_jvm_bp){
    this.host_jvm_bp=host_jvm_bp;
  }
  
  @Override
  public String toString(){
    return "GraderConfig{"
        +"id="+id
        +", name="+name
        +", enabled="+enabled
        +", timeout_seconds="+timeout_seconds
        +", concurrent_grading_processes="+concurrent_grading_processes
        +", logging_level="+logging_level
        +", subdir="+subdir
        +", backend_plugin_classname="+backend_plugin_classname
        +", relative_classpathes="+relative_classpathes
        +", fileextensions="+fileextensions
        +", grader_plugin_defaults="+grader_plugin_defaults
        +", operating_mode="+operating_mode
        +", docker_jvm_bp="+docker_jvm_bp
        +", host_jvm_bp="+host_jvm_bp+"}";
    }
}
