package backend.models;

public class Notification {
    private int id, userId;
    private String message, createdAt;
    private boolean isRead;

    public Notification() {}

    public int getId() { return id; }
    public void setId(int v) { id = v; }

    public int getUserId() { return userId; }
    public void setUserId(int v) { userId = v; }

    public String getMessage() { return message; }
    public void setMessage(String v) { message = v; }

    public boolean isRead() { return isRead; }
    public void setRead(boolean v) { isRead = v; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String v) { createdAt = v; }
}
