package app.cache;

import app.groups.GameTypeTag;

import java.time.DayOfWeek;
import java.util.TreeSet;

public class CompressedGroup {

  public int a;
  public String b;
  public String[] c;

  public String d;

  public TreeSet<DayOfWeek> e;

  public GameTypeTag[] f;

  public Integer getA() {
    return a;
  }

  public void setA(Integer a) {
    this.a = a;
  }

  public String getB() {
    return b;
  }

  public void setB(String b) {
    this.b = b;
  }

  public void setC(String[] c) {
    this.c = c;
  }

  public String[] getC() {
    if(c == null){
      return new String[0];
    }

    return c;
  }

  public String getD() {
    return d;
  }

  public void setD(String d) {
    this.d = d;
  }

  public TreeSet<DayOfWeek> getE(){return e;}

  public void setE(TreeSet<DayOfWeek> e) {
    this.e = e;
  }

  public void setF(GameTypeTag[] f){
    this.f = f;
  }

  public GameTypeTag[] getF(){
    return f;
  }
}
