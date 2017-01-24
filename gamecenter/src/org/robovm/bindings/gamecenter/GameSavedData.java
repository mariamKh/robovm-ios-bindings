package org.robovm.bindings.gamecenter;

import java.util.Date;

/**
 * Created by mariam on 1/23/17.
 */
public class GameSavedData {

    private String deviceName;

    private Date modificationDate;

    private String fileName;

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
