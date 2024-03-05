package io.resiliencebench.resources.queue;

import java.util.List;

public class ExecutionQueueSpec {

  private List<Item> items;
  private String benchmark;

  public boolean addItem(Item item) {
    return items.add(item);
  }

  public void setItems(List<Item> items) {
    this.items = items;
  }

  public List<Item> getItems() {
    return items;
  }
}
