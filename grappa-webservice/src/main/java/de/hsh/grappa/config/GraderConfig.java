package de.hsh.grappa.config;

import java.util.Properties;

public class GraderConfig {
  private String id;
  private String name;
  private boolean enabled;
  private int timeout_seconds = 120;
  private int concurrent_grading_processes = 5;
  private String logging_level;
  
  private String file_encoding;// UTF-8 
  private String user_language;// de
  private String user_country;// DE 
  
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
  
  public String getFile_encoding(){
	return file_encoding;
}

public void setFile_encoding(String file_encoding){
	this.file_encoding=file_encoding;
}

public String getUser_language(){
	return user_language;
}

public void setUser_language(String user_language){
	this.user_language=user_language;
}

public String getUser_country(){
	return user_country;
}

public void setUser_country(String user_country){
	this.user_country=user_country;
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
			+", file_encoding="+file_encoding
			+", user_language="+user_language
			+", user_country="+user_country
			+", grader_plugin_defaults="+grader_plugin_defaults
			+", operating_mode="+operating_mode
			+", docker_jvm_bp="+docker_jvm_bp
			+", host_jvm_bp="+host_jvm_bp
			+"}";
  }

}
