package app.data;

public class Data {
  private Group[] groups;
  private Convention[] conventions;
  private GameStore[] gameStores;
  private GameRestaurant[] gameRestaurants;

  public Data() {}

  public Group[] getGroups() {
    if(groups == null){
      return new Group[0];
    }
    return groups;
  }

  public void setGroups(Group[] groups) {
    this.groups = groups;
  }

  public Convention[] getConventions() {
    if(conventions == null){
      return new Convention[0];
    }
    return conventions;
  }

  public void setConventions(Convention[] conventions) {
    this.conventions = conventions;
  }

  public GameStore[] getGameStores() {
    if(gameStores == null){
      return new GameStore[0];
    }
    return gameStores;
  }

  public void setGameStores(GameStore[] gameStores) {
    this.gameStores = gameStores;
  }

  public GameRestaurant[] getGameRestaurants() {
    if(gameRestaurants == null){
      return new GameRestaurant[0];
    }
    return gameRestaurants;
  }

  public void setGameRestaurants(GameRestaurant[] gameRestaurants) {
    this.gameRestaurants = gameRestaurants;
  }
}
