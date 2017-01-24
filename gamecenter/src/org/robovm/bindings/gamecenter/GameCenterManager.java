
package org.robovm.bindings.gamecenter;

import java.util.ArrayList;

import org.robovm.apple.foundation.*;
import org.robovm.apple.gamekit.*;
import org.robovm.apple.uikit.UIDevice;
import org.robovm.apple.uikit.UIViewController;
import org.robovm.apple.uikit.UIWindow;
import org.robovm.objc.block.VoidBlock1;
import org.robovm.objc.block.VoidBlock2;
import org.robovm.objc.block.VoidBlock3;

@SuppressWarnings("deprecation")
public class GameCenterManager {
    public static final String GCM_DOMAIN = GameCenterManager.class.getSimpleName();
    public static final long GCM_ERROR_NOT_AUTHENTICATED = -1024;

    private static final int IOS_6 = 6;
    private static final int IOS_7 = 7;

    private final UIWindow keyWindow;
    private final GameCenterListener listener;

    private boolean isViewOpened;

    /** Constructor.
     * @param keyWindow KeyWindow can't be accessed from the Delegates sometimes, so we need to save a reference
     * @param listener */
    public GameCenterManager (UIWindow keyWindow, GameCenterListener listener) {
        this.keyWindow = keyWindow;
        this.listener = listener;
    }

    /** Do the login logic. If the user has never loged, a dialog will be shown. */
    public void login () {
        // If iOS version is 6 or more we use the new method
        if (getIosVersion() >= IOS_6) {
            GKLocalPlayer.getLocalPlayer().setAuthenticateHandler(new VoidBlock2<UIViewController, NSError>() {
                @Override
                public void invoke (UIViewController viewController, NSError error) {
                    // If the device does not have an authenticated player, show the login dialog
                    // This parameter is null if the authentication process is complete. Otherwise,
                    // it contains a view controller that your game should display to the player.
                    System.out.println("vc: "+viewController+" "+GKLocalPlayer.getLocalPlayer().isAuthenticated());
                    if (viewController != null) {
                        keyWindow.getRootViewController().presentViewController(viewController, true, null);
                        listener.onGCViewOpened();
                    }
                    // If the viewController is null and the player is authenticated, the login is completed
                    else if (GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
                        listener.playerLoginCompleted();
                    }
                    // If the viewController is null and the player is not authenticated the login has failed
                    else {
                        System.out.println("Error: "+error+".");
                        listener.playerLoginFailed(error);
                    }
                }
            });
        } else { // If iOS version is 5 or less we use the deprecated method
            GKLocalPlayer.getLocalPlayer().authenticate(new VoidBlock1<NSError>() {
                @Override
                public void invoke (NSError error) {
                    if (GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
                        listener.playerLoginCompleted();
                    } else {
                        listener.playerLoginFailed(error);
                    }
                }
            });
        }
    }

    /** Report an achievement completed (100 as percentComplete)
     * 
     * @param identifier */
    public void reportAchievement (String identifier) {
        reportAchievement(identifier, 100);
    }

    /** Report an achievement with a percentComplete
     * 
     * @param identifier
     * @param percentComplete */
    public void reportAchievement (final String identifier, double percentComplete) {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
            listener.achievementReportFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKAchievement achievement = new GKAchievement(identifier);
        achievement.setPercentComplete(percentComplete);
        achievement.setShowsCompletionBanner(true);

        // If iOS version is 6 or more we use the new method
        if (getIosVersion() >= IOS_6) {
            // Create an array with the achievement
            NSArray<GKAchievement> achievements = new NSArray<GKAchievement>(achievement);

            GKAchievement.reportAchievements(achievements, new VoidBlock1<NSError>() {
                @Override
                public void invoke (NSError error) {
                    if (error != null) {
                        listener.achievementReportFailed(error);
                    } else {
                        listener.achievementReportCompleted(identifier);
                    }
                }
            });
        } else { // If iOS version is 5 or less we use the deprecated method
            achievement.reportAchievement(new VoidBlock1<NSError>() {
                @Override
                public void invoke (NSError error) {
                    if (error != null) {
                        listener.achievementReportFailed(error);
                    } else {
                        listener.achievementReportCompleted(identifier);
                    }
                }
            });
        }
    }

    /** Load all the achievements for the local player */
    public void loadAchievements () {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
            listener.achievementsLoadFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKAchievement.loadAchievements(new VoidBlock2<NSArray<GKAchievement>, NSError>() {
            @Override
            public void invoke (NSArray<GKAchievement> array, NSError error) {
                if (error != null) {
                    listener.achievementsLoadFailed(error);
                } else {
                    ArrayList<GKAchievement> achievements = new ArrayList<GKAchievement>();
                    for (GKAchievement achievement : array) {
                        achievements.add(achievement);
                    }
                    listener.achievementsLoadCompleted(achievements);
                }
            }
        });
    }

    /** Reset the achievements progress for the local player. All the entries for the local player are removed from the server. */
    public void resetAchievements () {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
            listener.achievementsResetFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKAchievement.resetAchievements(new VoidBlock1<NSError>() {
            @Override
            public void invoke (NSError error) {
                if (error != null) {
                    listener.achievementsResetFailed(error);
                } else {
                    listener.achievementsResetCompleted();
                }
            }
        });
    }

    /** Report a score to GameCenter
     * @param identifier
     * @param score */
    public void reportScore (final String identifier, long score) {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
            listener.scoreReportFailed(buildUnauthenticatedPlayerError());
            return;
        }

        GKScore scoreReporter = new GKScore();
        scoreReporter.setValue(score);

        // If iOS version is 7 or more we use the new method
        if (getIosVersion() >= IOS_7) {
            scoreReporter.setLeaderboardIdentifier(identifier);
            NSArray<GKScore> scores = new NSArray<GKScore>(scoreReporter);

            GKScore.reportScores(scores, new VoidBlock1<NSError>() {
                @Override
                public void invoke (NSError error) {
                    if (error != null) {
                        listener.scoreReportFailed(error);
                    } else {
                        listener.scoreReportCompleted(identifier);
                    }
                }
            });
        } else { // If iOS version is 6 or less we use the deprecated method
            scoreReporter.setCategory(identifier);
            scoreReporter.reportScore(new VoidBlock1<NSError>() {
                @Override
                public void invoke (NSError error) {
                    if (error != null) {
                        listener.scoreReportFailed(error);
                    } else {
                        listener.scoreReportCompleted(identifier);
                    }
                }
            });
        }
    }

    /** Load all the Leaderboards for the Game. Warning: If using iOS5 or less the Leaderboard object will only include the
     * Category (identifier) */
    public void loadLeaderboards () {
        // If player is not authenticated, do nothing
        if (!GKLocalPlayer.getLocalPlayer().isAuthenticated()) {
            listener.leaderboardsLoadFailed(buildUnauthenticatedPlayerError());
            return;
        }

        // If iOS version is 6 or more we use the new method
        if (getIosVersion() >= IOS_6) {
            GKLeaderboard.loadLeaderboards(new VoidBlock2<NSArray<GKLeaderboard>, NSError>() {
                @Override
                public void invoke (NSArray<GKLeaderboard> array, NSError error) {
                    if (error != null) {
                        listener.leaderboardsLoadFailed(error);
                    } else {
                        ArrayList<GKLeaderboard> leaderboards = new ArrayList<GKLeaderboard>();
                        for (GKLeaderboard leaderboard : array) {
                            leaderboards.add(leaderboard);
                        }
                        listener.leaderboardsLoadCompleted(leaderboards);
                    }
                }
            });
        } else { // If iOS version is 6 or less we use the deprecated method
            GKLeaderboard.loadCategories(new VoidBlock3<NSArray<NSString>, NSArray<NSString>, NSError>() {
                @Override
                public void invoke (NSArray<NSString> array, NSArray<NSString> array2, NSError error) {
                    if (error != null) {
                        listener.leaderboardsLoadFailed(error);
                    } else {
                        ArrayList<GKLeaderboard> leaderboards = new ArrayList<GKLeaderboard>();
                        for (NSString category : array) {
                            GKLeaderboard leaderboard = new GKLeaderboard();
                            leaderboard.setCategory(category.toString());

                            leaderboards.add(leaderboard);
                        }
                        listener.leaderboardsLoadCompleted(leaderboards);
                    }
                }
            });
        }
    }

    /** Return the id of a leaderboard (category or identifier, depending on iOS version)
     * @param leaderboard
     * @return */
    public String getLeaderboardId (GKLeaderboard leaderboard) {
        if (getIosVersion() >= IOS_7) {
            return leaderboard.getIdentifier();
        } else {
            return leaderboard.getCategory();
        }
    }

    /** Shows GameCenter standard interface for Achievements */
    public void showAchievementsView () {
        // If iOS version is 6 or more we use the new method
        if (getIosVersion() >= IOS_6) {
            GKGameCenterViewController gameCenterView = new GKGameCenterViewController();
            gameCenterView.setGameCenterDelegate(new GKGameCenterControllerDelegateAdapter() {
                @Override
                public void didFinish (GKGameCenterViewController gameCenterViewController) {
                    dismissViewControllerAndNotifyListener(gameCenterViewController, GKGameCenterViewControllerState.Achievements);
                }
            });
            gameCenterView.setViewState(GKGameCenterViewControllerState.Achievements);
            presentViewController(gameCenterView);
        } else { // If iOS version is 6 or less we use the deprecated method
            GKAchievementViewController gameCenterView = new GKAchievementViewController();
            gameCenterView.setAchievementDelegate(new GKAchievementViewControllerDelegateAdapter() {
                @Override
                public void didFinish (GKAchievementViewController viewController) {
                    dismissViewControllerAndNotifyListener(viewController, GKGameCenterViewControllerState.Achievements);
                }
            });
            presentViewController(gameCenterView);
        }
    }

    /** Shows GameCenter standard interface for Leaderboards */
    public void showLeaderboardsView () {
        // If iOS version is 6 or more we use the new method
        if (getIosVersion() >= IOS_6) {
            GKGameCenterViewController gameCenterView = new GKGameCenterViewController();
            gameCenterView.setGameCenterDelegate(new GKGameCenterControllerDelegateAdapter() {
                @Override
                public void didFinish (GKGameCenterViewController gameCenterViewController) {
                    dismissViewControllerAndNotifyListener(gameCenterViewController, GKGameCenterViewControllerState.Leaderboards);
                }
            });
            gameCenterView.setViewState(GKGameCenterViewControllerState.Leaderboards);
            // gameCenterView.setLeaderboardIdentifier("CgkI4OvQqOcSEAIQBg");
            presentViewController(gameCenterView);
        } else { // If iOS version is 6 or less we use the deprecated method
            GKLeaderboardViewController gameCenterView = new GKLeaderboardViewController();
            gameCenterView.setTimeScope(GKLeaderboardTimeScope.AllTime);
            gameCenterView.setLeaderboardDelegate(new GKLeaderboardViewControllerDelegateAdapter() {
                @Override
                public void didFinish (GKLeaderboardViewController viewController) {
                    dismissViewControllerAndNotifyListener(viewController, GKGameCenterViewControllerState.Leaderboards);
                }
            });
            presentViewController(gameCenterView);
        }
    }

    /** Shows GameCenter standard interface for one Leaderboard
     * @param identifier */
    public void showLeaderboardView (String identifier) {
        // If iOS version is 6 or more we use the new method
        if (getIosVersion() >= IOS_6) {
            GKGameCenterViewController gameCenterView = new GKGameCenterViewController();
            gameCenterView.setGameCenterDelegate(new GKGameCenterControllerDelegateAdapter() {
                @Override
                public void didFinish (GKGameCenterViewController gameCenterViewController) {
                    dismissViewControllerAndNotifyListener(gameCenterViewController, GKGameCenterViewControllerState.Leaderboards);
                }
            });
            gameCenterView.setViewState(GKGameCenterViewControllerState.Leaderboards);
            if (getIosVersion() >= IOS_7)
                gameCenterView.setLeaderboardIdentifier(identifier);
            else
                gameCenterView.setLeaderboardCategory(identifier);

            presentViewController(gameCenterView);
        } else { // If iOS version is 6 or less we use the deprecated method
            GKLeaderboardViewController gameCenterView = new GKLeaderboardViewController();
            gameCenterView.setCategory(identifier);
            gameCenterView.setTimeScope(GKLeaderboardTimeScope.AllTime);
            gameCenterView.setLeaderboardDelegate(new GKLeaderboardViewControllerDelegateAdapter() {
                @Override
                public void didFinish (GKLeaderboardViewController viewController) {
                    dismissViewControllerAndNotifyListener(viewController, GKGameCenterViewControllerState.Leaderboards);
                }
            });

            presentViewController(gameCenterView);
        }
    }

    public void saveGameData(byte[] data, String name) {
        NSData nsData = new NSData(data);
        GKLocalPlayer.getLocalPlayer().saveGameData(nsData, name, new VoidBlock2<GKSavedGame, NSError>() {
            @Override
            public void invoke(GKSavedGame gkSavedGame, NSError nsError) {
                if (nsError != null) {
                    listener.saveGameDataFailed(nsError);
                } else {
                    GameSavedData savedData = getSavedData(gkSavedGame);
                    listener.saveGameDataSucceeded(savedData);
                }
            }
        });
    }

    public void loadGameData() {
        GKLocalPlayer.getLocalPlayer().fetchSavedGames(new VoidBlock2<NSArray<GKSavedGame>, NSError>() {
            @Override
            public void invoke(NSArray<GKSavedGame> gkSavedGames, NSError error) {

                if (error != null) {
                    listener.loadGameDataFailed(error);
                } else {
                    if (gkSavedGames.size() > 0) {
                        GameSavedData savedData = getSavedData(gkSavedGames.get(0));
                        listener.loadGameDataSucceeded(savedData);
                    }
                }


//                NSArray<GKSavedGame> conflictingGames = new NSArray<GKSavedGame>();
//                for (int i=0; i<gkSavedGames.size(); i++) {
//                    GKSavedGame iGame = gkSavedGames.get(i);
//                    conflictingGames.clear();
//                    for (int j=i+1; j<gkSavedGames.size(); j++) {
//                        GKSavedGame jGame = gkSavedGames.get(j);
//                        if (iGame.getName().equals(jGame.getName())) {
//                            conflictingGames.add(jGame);
//                        }
//                    }
//                    if (conflictingGames.size() > 0) {
//                        conflictingGames.add(iGame);
//                        GKLocalPlayer.getLocalPlayer().resolveConflictingSavedGames(conflictingGames);
//                    }
//                }
            }
        });
    }

    /** Dismiss the {@link UIViewController} and invoke the appropriate callback on the {@link #listener}.
     * 
     * @param viewController the {@link UIViewController} to dismiss
     * @param viewControllerState the type of the View Controller being dismissed */
    private void dismissViewControllerAndNotifyListener (UIViewController viewController,
        final GKGameCenterViewControllerState viewControllerState) {
        viewController.dismissViewController(true, new Runnable() {
            @Override
            public void run () {
                isViewOpened = false;

                switch (viewControllerState) {
                case Achievements:
                    listener.achievementViewDismissed();
                    break;
                case Leaderboards:
                    listener.leaderboardViewDismissed();
                    break;
                default:
                    break;
                }
            }
        });
    }

    /** Returns the iOS version of the current device
     * 
     * @return */
    private int getIosVersion () {
        String systemVersion = UIDevice.getCurrentDevice().getSystemVersion();
        int version = Integer.parseInt(systemVersion.substring(0, systemVersion.indexOf('.')));
        return version;
    }

    /** Generate an {@link NSError} indicating that the local player is unauthenticated.
     * 
     * @return {@link NSError} */
    private NSError buildUnauthenticatedPlayerError () {
        NSErrorUserInfo info = new NSErrorUserInfo().setLocalizedDescription("Local player is unauthenticated");
        return new NSError(GCM_DOMAIN, GCM_ERROR_NOT_AUTHENTICATED, info);
    }

    private void presentViewController(UIViewController viewController) {
        keyWindow.getRootViewController().presentViewController(viewController, true, null);
        listener.onGCViewOpened();
        isViewOpened = true;
    }

    public boolean isViewOpened() {
        return isViewOpened;
    }


    private GameSavedData getSavedData (GKSavedGame gkSavedGame) {
        GameSavedData savedData = new GameSavedData();
        savedData.setFileName(gkSavedGame.getName());
        savedData.setDeviceName(gkSavedGame.getDeviceName());
        savedData.setModificationDate(gkSavedGame.getModificationDate().toDate());
        return savedData;
    }

}
