package com.quicklink.javalinfly.openapi.model;

public class Info {

  public String title;
  public String version;
  public Contact contact;

  public Info(String title, String version, Contact contact) {
    this.title = title;
    this.version = version;
    this.contact = contact;
  }


  public static class Contact {
    public String name;
    public String url;
    public String email;

    public Contact(String name, String url, String email) {
      this.name = name;
      this.url = url;
      this.email = email;
    }
  }

}
