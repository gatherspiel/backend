package app.admin.request;

public class BulkUpdateInputRequest {
  private BulkUpdateRequest data;
  private String email;
  private String password;

  public BulkUpdateInputRequest() {}

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public BulkUpdateRequest getData() {
    return data;
  }

  public void setData(BulkUpdateRequest data) {
    this.data = data;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
