package de.hsh.grappa.config;

public class GraderDockerJvmBpConfig{
	private String image_name;
	private String username;
	private String password_pat;
	
	private String copy_submission_to_dir_path;
	private String load_response_from_dir_path;
	private String copy_grader_plugin_defaults_properties_to_file;
	
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
	public String getCopy_submission_to_dir_path(){
		return copy_submission_to_dir_path;
	}
	public void setCopy_submission_to_dir_path(String copy_submission_to_dir_path){
		this.copy_submission_to_dir_path=copy_submission_to_dir_path;
	}
	public String getLoad_response_from_dir_path(){
		return load_response_from_dir_path;
	}
	public void setLoad_response_from_dir_path(String load_response_from_dir_path){
		this.load_response_from_dir_path=load_response_from_dir_path;
	}
	public String getCopy_grader_plugin_defaults_properties_to_file(){
		return copy_grader_plugin_defaults_properties_to_file;
	}
	public void setCopy_grader_plugin_defaults_properties_to_file(String copy_grader_plugin_defaults_properties_to_file){
		this.copy_grader_plugin_defaults_properties_to_file=copy_grader_plugin_defaults_properties_to_file;
	}

	@Override
	public String toString(){
		return "GraderDockerJvmBpConfig{"
				+"image_name="+image_name
				+", username="+username
				+", password_pat="+password_pat
				+", copy_submission_to_dir_path="+copy_submission_to_dir_path
				+", load_response_from_dir_path="+load_response_from_dir_path
				+", copy_grader_plugin_defaults_properties_to_file="+copy_grader_plugin_defaults_properties_to_file
				+"}";
	}
}
