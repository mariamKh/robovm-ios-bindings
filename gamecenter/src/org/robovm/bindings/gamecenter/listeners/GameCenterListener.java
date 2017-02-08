
package org.robovm.bindings.gamecenter.listeners;

import java.util.ArrayList;

import org.robovm.apple.foundation.NSError;
import org.robovm.apple.gamekit.GKAchievement;
import org.robovm.apple.gamekit.GKLeaderboard;
import org.robovm.bindings.gamecenter.GameSavedData;
import org.robovm.bindings.gamecenter.signature.GKPlayerSignature;

/** Listener for GameCenter events */
public interface GameCenterListener {

    void onGCViewOpened();

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
}
