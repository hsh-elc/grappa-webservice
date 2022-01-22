package de.hsh.grappa.config;

import java.util.List;

public class GraderConfig {
    private GraderID id;
    private String display_name;
    private List<String> proglangs;
    private boolean enabled;
    private String class_path;
    private String file_extension;
    private String class_name;
    private String config_path;
    private int timeout_seconds = 120;
    private int concurrent_grading_processes = 5;
    private String logging_level;
    private ResultSpecConfig result_spec;

    public GraderID getId() {
        return id;
    }

    public void setId(GraderID id) {
        this.id = id;
    }

    public String getDisplay_name() {
        return display_name;
    }

    public void setDisplay_name(String name) {
        this.display_name = name;
    }
  
    public List<String> getProglangs() {
	    return proglangs;
    }
    
    public void setProglangs(List<String> proglangs) {
    	this.proglangs = proglangs;
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

  	public String getLogging_level() {
	  return logging_level;
  	}

  	public void setLogging_level(String logging_level) {
  		this.logging_level = logging_level;
  	}
  
  	public ResultSpecConfig getResult_spec() {
  		return result_spec;
  	}
  
  	public void setResult_spec(ResultSpecConfig result_spec) {
  		this.result_spec = result_spec;
  	}

  	@Override
  	public String toString() {
  		return "Grader{" +
  				"id=" + id.toConfigString() +
  				", display_name=" + display_name + '\'' +
  				", proglangs=" + proglangs +
  				", enabled='" + enabled + '\'' +
  				", class_path=" + class_path + '\'' +
  				", file_extension=" + file_extension + '\'' +
  				", class_name='" + class_name + '\'' +
  				", config_path='" + config_path + '\'' +
  				", max_runtime_seconds=" + timeout_seconds +
  				", max_concurrent_grade_processes=" + concurrent_grading_processes +
  				", logging_level=" + logging_level +
  				", result_spec=" + result_spec + 
  				'}';
    }
}
