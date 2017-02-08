
package org.robovm.bindings.gamecenter;

import java.util.ArrayList;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.gamekit.GKAchievement;
import org.robovm.apple.gamekit.GKLeaderboard;

/** Listener for GameCenter events */
public interface GameCenterListener {

    void onGCViewOpened();

    void playerLoginCompleted ();

    void playerLoginFailed (NSError error);

    void achievementReportCompleted (String identifier);

    void achievementReportFailed (NSError error);

    void achievementsLoadCompleted (ArrayList<GKAchievement> achievements);

    void achievementsLoadFailed (NSError error);

    void achievementsResetCompleted ();

    void achievementsResetFailed (NSError error);

    void scoreReportCompleted (String identifier);

    void scoreReportFailed (NSError error);

    void leaderboardsLoadCompleted (ArrayList<GKLeaderboard> scores);

    void leaderboardsLoadFailed (NSError error);

    void leaderboardViewDismissed ();

    void achievementViewDismissed ();

    void saveGameDataSucceeded(GameSavedData savedData);

    void saveGameDataFailed(NSError error);

    void loadGameDataSucceeded(GameSavedData savedData, byte[] data);

    void loadGameDataFailed(NSError error);

    void generateIdentityVerificationSignatureSucceded(GKPlayerSignature gkPlayerSignature);

    void generateIdentityVerificationSignatureFailed (NSError error);
}
