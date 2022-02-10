package de.hsh.grappa.config;

public class ResultSpecConfig {
	private String format;
	private String structure;
	private String teacher_feedback_level;
	private String student_feedback_level;
	
	public String getFormat() {
		return format;
	}
	
	public void setFormat(String format) {
		this.format = format;
	}

	public String getStructure() {
		return structure;
	}

	public void setStructure(String structure) {
		this.structure = structure;
	}
	
	public String getTeacher_feedback_level() {
		return teacher_feedback_level;
	}
	
	public void setTeacher_feedback_level(String teacher_feedback_level) {
		this.teacher_feedback_level = teacher_feedback_level;
	}
	
	public String getStudent_feedback_level() {
		return student_feedback_level;
	}
	
	public void setStudent_feedback_level(String student_feedback_level) {
		this.student_feedback_level = student_feedback_level;
	}
	
	@Override
	public String toString() {
		return "ResultSpec{" + 
				"format='" + format + '\'' +
				", structure='" + structure + '\'' +
				", teacher_feedback_level='" + teacher_feedback_level + '\'' +
				", student_feedback_level='" + student_feedback_level + '\'' +
				"}";
	}
}