package bart.friendfinderapp.invitation;

/**
 * Created by Godzio on 2015-12-05.
 */
public class Invitation {

    private final int id;
    private final String inviterId;
    private final String inviterUsername;
    private final String sentAt;

    public Invitation( int id, String inviterId, String inviterUsername, String sentAt ) {
        this.id = id;
        this.inviterId = inviterId;
        this.inviterUsername = inviterUsername;
        this.sentAt = sentAt;
    }

    public int getId() {
        return id;
    }

    public String getInviterId() {
        return inviterId;
    }

    public String getInviterUsername() {
        return inviterUsername;
    }

    public String getSentAt() {
        return sentAt;
    }
}
