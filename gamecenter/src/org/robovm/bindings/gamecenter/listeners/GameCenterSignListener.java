package org.robovm.bindings.gamecenter.listeners;

import org.robovm.apple.foundation.NSError;
import org.robovm.bindings.gamecenter.signature.GKPlayerSignature;

/**
 * Created by mariam on 2/8/17.
 */
public interface GameCenterSignListener {

    void onGCViewOpened();

    void playerLoginCompleted ();

    void playerLoginFailed (NSError error);

    void generateIdentityVerificationSignatureSucceded(GKPlayerSignature gkPlayerSignature);

    void generateIdentityVerificationSignatureFailed (NSError error);
}
