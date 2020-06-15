package de.hsh.grappa.config;

import java.util.List;

public class GrappaConfig {
  private ServiceConfig service;
  private List<LmsConfig> lms;
  private List<GraderConfig> graders;
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

  //public boolean graderIdExists

  @Override
  public String toString() {
    return "GrappaConfig{" +
            "service=" + service +
            ", lms=" + lms +
            ", graders=" + graders +
            ", cache=" + cache +
            '}';
  }
}
