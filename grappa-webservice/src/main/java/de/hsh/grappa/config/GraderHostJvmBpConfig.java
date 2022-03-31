package de.hsh.grappa.config;

public class GraderHostJvmBpConfig{
	private String additional_absolute_classpathes;
	private String dir;
	private String fileextensions;
	private String backend_plugin_classname;
	
	public String getAdditional_absolute_classpathes(){
		return additional_absolute_classpathes;
	}
	public void setAdditional_absolute_classpathes(String additional_absolute_classpathes){
		this.additional_absolute_classpathes=additional_absolute_classpathes;
	}
	public String getDir(){
		return dir;
	}
	public void setDir(String dir){
		this.dir=dir;
	}
	public String getFileextensions(){
		return fileextensions;
	}
	public void setFileextensions(String fileextensions){
		this.fileextensions=fileextensions;
	}
	public String getBackend_plugin_classname(){
		return backend_plugin_classname;
	}
	public void setBackend_plugin_classname(String backend_plugin_classname){
		this.backend_plugin_classname=backend_plugin_classname;
	}
	@Override
	public String toString(){
		return "GraderHostJvmBpConfig{"
				+"additional_absolute_classpathes="+additional_absolute_classpathes
				+", dir="+dir
				+", fileextensions="+fileextensions
				+", backend_plugin_classname="+backend_plugin_classname
				+"}";
	}
}
