package app.result;

import app.location.Convention;
import app.location.GameRestaurant;
import app.location.GameStore;

import java.util.Map;
import java.util.TreeMap;

public class GameLocationData {
  public TreeMap<String, Convention> conventions;
  public TreeMap<String,GameRestaurant> gameRestaurants;
  public TreeMap<String,GameStore> gameStores;

  public GameLocationData(){}

  public TreeMap<String,Convention> getConventions(){
    return conventions;
  }

  public void setConventions(TreeMap<String,Convention> conventions){
    this.conventions = conventions;
  }

  public TreeMap<String,GameRestaurant> getGameRestaurants(){
    return gameRestaurants;
  }

  public void setGameRestaurants(TreeMap<String,GameRestaurant> gameRestaurants){
    this.gameRestaurants = gameRestaurants;
  }

  public Map<String,GameStore> getGameStores(){
    return gameStores;
  }

  public void setGameStores(TreeMap<String,GameStore>  gameStores){
    this.gameStores = gameStores;
  }
}
