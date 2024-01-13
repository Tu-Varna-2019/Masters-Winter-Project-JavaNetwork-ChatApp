package model;

public class FriendRequest {
    private int id;
    private String status;
    private User sender;
    private User recipient;

    public FriendRequest() {
    }

    public FriendRequest(int id, String status, User sender, User recipient) {
        this.id = id;
        this.status = status;
        this.sender = sender;
        this.recipient = recipient;

    }

    public int getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public User getSender() {
        return sender;
    }

    public User getRecipient() {
        return recipient;
    }

}
