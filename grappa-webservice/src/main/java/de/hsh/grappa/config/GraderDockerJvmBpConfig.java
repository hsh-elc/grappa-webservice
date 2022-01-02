package de.hsh.grappa.config;

public class GraderDockerJvmBpConfig{
	private String image_name;
	private String username;
	private String password_pat;
	public String getImage_name(){
		return image_name;
	}
	public void setImage_name(String image_name){
		this.image_name=image_name;
	}
	public String getUsername(){
		return username;
	}
	public void setUsername(String username){
		this.username=username;
	}
	public String getPassword_pat(){
		return password_pat;
	}
	public void setPassword_pat(String password_pat){
		this.password_pat=password_pat;
	}
	@Override
	public String toString(){
		return "GraderDockerJvmBpConfig{"
				+"image_name="+image_name
				+", username="+username
				+", password_pat="+password_pat+"}";
	}

}
