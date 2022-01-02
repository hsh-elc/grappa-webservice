package de.hsh.grappa.config;

public class DockerProxyConfig{
	private String class_path;
	private String class_name;
	private String host;

	public String getClass_path(){
		return class_path;
	}

	public void setClass_path(String class_path){
		this.class_path=class_path;
	}

	public String getClass_name(){
		return class_name;
	}

	public void setClass_name(String class_name){
		this.class_name=class_name;
	}

	public String getHost(){
		return host;
	}

	public void setHost(String host){
		this.host=host;
	}

	@Override
	public String toString(){
		return "DockerProxyConfig{"
				+"class_path="+class_path
				+", class_name="+class_name
				+", host="+host+"}";
	}
}
