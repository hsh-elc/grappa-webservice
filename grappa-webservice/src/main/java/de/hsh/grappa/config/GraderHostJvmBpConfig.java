package de.hsh.grappa.config;

public class GraderHostJvmBpConfig{
	private String hostonly_classpathes;
	private String plugin_jar_name;
	
	public String getHostonly_classpathes(){
		return hostonly_classpathes;
	}
	public void setHostonly_classpathes(String hostonly_classpathes){
		this.hostonly_classpathes=hostonly_classpathes;
	}
	public String getPlugin_jar_name(){
		return plugin_jar_name;
	}
	public void setPlugin_jar_name(String plugin_jar_name){
		this.plugin_jar_name=plugin_jar_name;
	}
	@Override
	public String toString(){
		return "GraderHostJvmBpConfig{"
				+"hostonly_classpathes="+hostonly_classpathes
				+", plugin_jar_name="+plugin_jar_name+"}";
	}

}
