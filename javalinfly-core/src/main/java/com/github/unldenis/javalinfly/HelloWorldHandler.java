package com.github.unldenis.javalinfly;

import io.javalin.http.Context;

@Controller(path = "/helloworld")
public class HelloWorldHandler {

  class Person {
    String name;
    int age;

    public Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
  }

  @Get
  public Response<Person, String> helloPerson(Context ctx) {
    return Response.ok(new Person("Joe", 25));
  }

}
