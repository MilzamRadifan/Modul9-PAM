package com.example.modul9;

public class Note {
  private String key;
  private String title;
  private String description;
  public Note() {
  }
  public Note(String title, String description) {
    this.title = title;
    this.description = description;
  }

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public String getTitle() {
    return title;
  }
  public void setTitle(String title) {
    this.title = title;
  }
  public String getDescription() {
    return description;
  }
  public void setDescription(String description) {
    this.description = description;
  }
}
