package org.robovm.bindings.gamecenter.listeners;

import org.robovm.apple.foundation.NSError;
import org.robovm.bindings.gamecenter.GameSavedData;

/**
 * Created by mariam on 2/8/17.
 */
public interface CloudDataListener {

    void saveGameDataSucceeded(GameSavedData savedData);

    void saveGameDataFailed(NSError error);

    void loadGameDataSucceeded(GameSavedData savedData, byte[] data);

    void loadGameDataFailed(NSError error);
}
