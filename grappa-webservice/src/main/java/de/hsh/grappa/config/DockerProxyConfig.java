package de.hsh.grappa.config;

public class DockerProxyConfig{
	private String host;

	public String getHost(){
		return host;
	}

	public void setHost(String host){
		this.host=host;
	}

	@Override
	public String toString(){
		return "DockerProxyConfig{"
				+", host="+host+"}";
	}
}
