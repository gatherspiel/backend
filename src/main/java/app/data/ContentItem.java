package app.data;

import java.util.UUID;

public abstract class ContentItem {

  UUID uuid;
  ContentType contentType;

  public ContentItem(ContentType contentType){
    this.uuid = UUID.randomUUID();
    this.contentType = contentType;
  }

  public ContentItem(UUID uuid){
    this.uuid = uuid;
  }

  public UUID getUUID() {

    return this.uuid;
  }

  public void setUuid(UUID uuid){
    this.uuid = uuid;
  }
}
