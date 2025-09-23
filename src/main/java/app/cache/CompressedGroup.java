package app.cache;


public class CompressedGroup {

  public int a;
  public String b;
  public String[] c;

  public String d;

  public Boolean e;
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

  public Boolean getE(){return e;}

  public void setE(Boolean e) {
    this.e = e;
  }
}
