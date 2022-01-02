package de.hsh.grappa.config;

import java.util.List;

import com.google.common.base.Strings;

public class GrappaConfig {
  private ServiceConfig service;
  private List<LmsConfig> lms;
  
  private String graders_home;
  private List<GraderConfig> graders;
  private DockerProxyConfig docker_proxy;
  
  private CacheConfig cache;

  public ServiceConfig getService() {
    return service;
  }

  public void setService(ServiceConfig service) {
    this.service = service;
  }

  public List<LmsConfig> getLms() {
    return lms;
  }

  public void setLms(List<LmsConfig> lms) {
    this.lms = lms;
  }

  public List<GraderConfig> getGraders() {
    return graders;
  }

  public void setGraders(List<GraderConfig> graders) {
    this.graders = graders;
  }

  public CacheConfig getCache() {
    return cache;
  }

  public void setCache(CacheConfig cache) {
    this.cache = cache;
  }

  public String getGraders_home(){
    return graders_home;
  }
  
  public void setGraders_home(String graders_home){
    this.graders_home=graders_home;
  }
  
  public DockerProxyConfig getDocker_proxy(){
    return docker_proxy;
  }
  
  public void setDocker_proxy(DockerProxyConfig docker_proxy){
    this.docker_proxy=docker_proxy;
  }
  
  //public boolean graderIdExists
  
  @Override
    public String toString(){
      return "GrappaConfig{"
        +"service="+service
        +", lms="+lms
        +", graders_home="+graders_home
        +", graders="+graders
        +", docker_proxy="+docker_proxy
        +", cache="+cache+"}";
  }
  
  public void propagateLoggingLevelToGraders() {
    for (GraderConfig g : graders) {
      if (Strings.isNullOrEmpty(g.getLogging_level())) {
        g.setLogging_level(service.getLogging_level());
      }
    }
  }
}
