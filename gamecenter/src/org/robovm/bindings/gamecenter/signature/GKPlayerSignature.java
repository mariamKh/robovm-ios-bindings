package org.robovm.bindings.gamecenter.signature;

/**
 * Created by mariam on 2/8/17.
 */
public class GKPlayerSignature {

    private String playerID;
    private String bundleID;

    private String playerUserName;
    private String playerDisplayName;

    private String publicKeyUrl;
    private byte[] signature;
    private byte[] salt;
    private long timestamp;

    public String getPlayerID() {
        return playerID;
    }

    public void setPlayerID(String playerID) {
        this.playerID = playerID;
    }

    public String getBundleID() {
        return bundleID;
    }

    public void setBundleID(String bundleID) {
        this.bundleID = bundleID;
    }

    public String getPublicKeyUrl() {
        return publicKeyUrl;
    }

    public void setPublicKeyUrl(String publicKeyUrl) {
        this.publicKeyUrl = publicKeyUrl;
    }

    public byte[] getSignature() {
        return signature;
    }

    public void setSignature(byte[] signature) {
        this.signature = signature;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getPlayerUserName() {
        return playerUserName;
    }

    public void setPlayerUserName(String playerUserName) {
        this.playerUserName = playerUserName;
    }

    public String getPlayerDisplayName() {
        return playerDisplayName;
    }

    public void setPlayerDisplayName(String playerDisplayName) {
        this.playerDisplayName = playerDisplayName;
    }

    @Override
    public String toString() {
        return "player ID: " + playerID
                + ", bundle ID: " + bundleID
                + ", publicKeyUrl: " + publicKeyUrl
                + ", signature: " + signature
                + ", salt: "+salt
                + ", timestamp: "+timestamp
                + ", alias: "+ playerUserName
                + ", display name: "+playerDisplayName;
    }
}
