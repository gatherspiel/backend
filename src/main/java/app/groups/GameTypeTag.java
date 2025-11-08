package app.groups;


public enum GameTypeTag {
  EUROGAMES("Eurogames"),
  LIGHT_GAMES("Light games"),
  SOCIAL_GAMES("Social games"),
  WARGAMES("Wargames"),
  HIDDEN_IDENTITY_GAMES("Hidden identity games");

  private String name;


  GameTypeTag(String name){
    this.name = name;
  }

  @Override
  public String toString(){
    return name;
  }

}