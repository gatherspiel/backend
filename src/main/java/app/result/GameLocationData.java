package app.result;

import app.location.Convention;
import app.location.GameRestaurant;
import app.location.GameStore;

import java.util.Map;

public class GameLocationData {
  public Map<Integer, Convention> conventions;
  public Map<Integer,GameRestaurant> gameRestaurants;
  public Map<Integer,GameStore> gameStores;

  public GameLocationData(){}

  public Map<Integer,Convention> getConventions(){
    return conventions;
  }

  public void setConventions(Map<Integer,Convention> conventions){
    this.conventions = conventions;
  }

  public Map<Integer,GameRestaurant> getGameRestaurants(){
    return gameRestaurants;
  }

  public void setGameRestaurants(Map<Integer,GameRestaurant> gameRestaurants){
    this.gameRestaurants = gameRestaurants;
  }

  public Map<Integer,GameStore> getGameStores(){
    return gameStores;
  }

  public void setGameStores(Map<Integer,GameStore>  gameStores){
    this.gameStores = gameStores;
  }
}
