package com.github.unldenis.javalinfly.processor;

public abstract class Round {

  private boolean executed = false;

  public void execute() {
    if(executed()) {
      return;
    }
    executed = true;
    run();

  }

  protected abstract void run();

  public boolean executed() {
    return executed;
  }
}
