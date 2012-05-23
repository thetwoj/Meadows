[33mcommit 57b9e0e5efcfa782bcca748ce653f1533737baef[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Wed Apr 11 23:47:34 2012 -0700

    Fixed a force-close issue that occured when backing out of the app after it had been restarted following being cleaned up by system

[33mcommit 2ca3395357953d9aff1ffa5378c6fd0d9eba211d[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Wed Apr 11 17:55:23 2012 -0700

    Changed the main activity to not halt the service upon its own destruction, added check to see if service is running to prevent doubling-up on re-creation of activity

[33mcommit 51c3a0c0d5df78ce49c0b18d13f8a22aa4ab78d0[m
Merge: b97ef64 55fc99c
Author: JJ Graham <thetwoj@gmail.com>
Date:   Wed Apr 11 16:58:23 2012 -0700

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit b97ef64a69daad4d0a018d24b0143a6a0ca3b5da[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Wed Apr 11 16:58:18 2012 -0700

    Fixed FC error resulting from auto-login

[33mcommit 55fc99c074f220ab173102944b6ea2fc50c3ad1a[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Wed Apr 11 15:18:21 2012 -0700

    Added Client.LogOut()

[33mcommit 605566f484ad130b523db608ddd168cb1d6dec3a[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Mon Apr 9 10:30:08 2012 -0700

    GPS now polls 5 times a second, only readings with accuracy <20m are used by the app

[33mcommit 5c5715f19f5cfe5c8217edaa5f4995f758daf2f6[m
Merge: ef366ef 722913c
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Mon Apr 9 02:22:43 2012 -0700

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit ef366ef0b8070ad7df9f7b01b13095416b9cb757[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Mon Apr 9 02:22:25 2012 -0700

    Minor code refactring

[33mcommit 722913c53093250aba234fa4ebe341e8359de138[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Tue Apr 3 20:39:02 2012 -0700

    Added more choices to frequency of updates in settings

[33mcommit d3c0b1b69b6e84e00b58e73fcf72cb1d00a7d30c[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Tue Apr 3 16:22:50 2012 -0700

    Added missing files

[33mcommit 0bc9a83bde493933cc8650f14e5bfba40ba3846b[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Sun Apr 1 22:39:01 2012 -0700

    Implemented meeting points

[33mcommit fb7a95b09de2a845bac4038252420e09430a75d3[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Mon Mar 19 17:01:48 2012 -0700

    Server.UpdateLocation now cancels any previous update requests if they can't connect

[33mcommit c96c612b4b26b471625b41b6182ebbff5a855fa8[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Thu Mar 15 19:57:23 2012 -0700

    Added lat/long to meeting points

[33mcommit 5d886f83303efce9398613b3c2944713ff278cdd[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Sun Mar 11 19:15:55 2012 -0700

    Added missing file

[33mcommit d7a346d7fd57ee7dc6a697bdfd9faa8d346774ce[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Thu Mar 8 16:16:44 2012 -0800

    Added Client.RemoveFriend and began implementation of meeting points

[33mcommit 1ee258bf6b327ace4445f048d102228785e7cf41[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Mar 1 20:57:33 2012 -0800

    Use client member instead of GetInstance in the service.

[33mcommit 3f5de2f47e39fa543ae031622c29b46b67ee3b72[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Wed Feb 29 23:44:17 2012 -0800

    Attempted to fix client loggout bug

[33mcommit 29591418e99f6dfe2901558a1326b7a97976948f[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Tue Feb 28 15:59:37 2012 -0800

    Added status bar notification for ClientLocationService

[33mcommit 12056301ebac3571801b0cd23eba6a361bece7a7[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Tue Feb 28 12:49:37 2012 -0800

    Forgot to add xml for preferences in last commit

[33mcommit 19f9f482db05273538c91e84d5c499d685c06b2a[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Tue Feb 28 12:48:34 2012 -0800

    Fixed issue with service persisting through close and multiple instances occuring. Added preferences page to select frequency of updates.

[33mcommit 93825377e19608cde4565c1226e0e168d661be04[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Sat Feb 25 04:15:00 2012 -0800

    Notifications, requests page, network check fix, etc

[33mcommit b342350afbc82af8abb6e5aa62749db9c73705fa[m
Merge: cf95a22 0fecd26
Author: JJ Graham <thetwoj@gmail.com>
Date:   Sat Feb 25 02:44:48 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit cf95a229484361295f2b71ba8f56c4c9191a5d06[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Sat Feb 25 02:44:42 2012 -0800

    Request notification count, add friend, accept/deny requests

[33mcommit 0fecd26e093e01739db49d659c918b8dc286bff7[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Sat Feb 25 01:04:04 2012 -0800

    Fixed deny friend request failure

[33mcommit 93b173a758d89cd80175252beb9a8dc384fd8257[m
Merge: c2b7093 6658a8c
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Fri Feb 24 22:06:00 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit c2b7093da2a86ec845ac1e5b09e77870e2fe8ea5[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Fri Feb 24 22:05:40 2012 -0800

    The server can now handle spotty connections.

[33mcommit c87286bfb54e3a016ee61250cb61b532a26f933b[m
Merge: f2c9e8e 6658a8c
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Feb 24 18:09:16 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit f2c9e8ed4da78951d55ea2843fea800151a3445b[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Feb 24 18:09:09 2012 -0800

    Working to add header to Friends page

[33mcommit 6658a8c4a9c7b386d1e78aa09297ea5682964ecc[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Feb 24 18:07:11 2012 -0800

    Fixed an issue with trying to draw a nameplate outside of the map.

[33mcommit 10fc0d95dc69c8d6ac4c0218ef98fe9057816496[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Feb 24 17:40:49 2012 -0800

    Removed print line

[33mcommit 2665788eef2c0e8790bf835d5206c0b09230ca58[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Feb 24 17:37:34 2012 -0800

    Ability to set visibility to users, friends alpha sorted

[33mcommit ea0f743a3ec079eb0e5ffc53921d732084a4fe05[m
Merge: bc2dcc0 25a851f
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Fri Feb 24 17:16:28 2012 -0800

    fixed stupid gitthing

[33mcommit bc2dcc0121ee28bb258cca78d5400315a71b67af[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Fri Feb 24 17:15:18 2012 -0800

    Swapped shareLocation/locationShared with shareWithClient and shareWithUser

[33mcommit 25a851fc130e2f0111206072296f25a8946372d1[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Feb 24 16:14:56 2012 -0800

    Commented out all the meeting point stuff for now. Fixed nameplate coloring when map page is opened.

[33mcommit cd2d49537ce026d902a53dcac0f497a636f50c9d[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Feb 24 15:03:16 2012 -0800

    Added periodic friend and request updates to client location service.

[33mcommit 012e4b29e22dfa2169e74548ce19c7a70053f9e1[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Feb 24 12:37:29 2012 -0800

    Changed from String.empty() to String.length() <= 0.

[33mcommit 07d73c75658567f096634c0f43c2d4cdc5211c1b[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Feb 24 12:36:07 2012 -0800

    Updates to Friend layouts, scrollview now clickable to accept requests

[33mcommit 7ff831cc9916e9cf59754b6ecc1bbd69787e7a49[m
Merge: 5666668 4ca795a
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Feb 23 00:11:50 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit 5666668d299b3d6be2e3402004de4953b99f6567[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Feb 23 00:11:29 2012 -0800

    Added FriendAdapter and list_item.xml files

[33mcommit 4ca795a9638bb16015493b698da1587cf49867c2[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Wed Feb 22 22:20:35 2012 -0800

    Cleaned up some code.

[33mcommit 86a87639b531681173edd03153f3d438e70033c6[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Wed Feb 22 21:54:38 2012 -0800

    Commented out code causing failure in Friends

[33mcommit 162b1f4516d6135ade19fec6485947438a4f9bea[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Tue Feb 21 16:10:22 2012 -0800

    Implemented secure hashing, auto-login

[33mcommit ba525bd54dbdaf4a8a8d7bae33a325300a0e52b9[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Tue Feb 21 01:53:11 2012 -0800

    Added registration/login activities

[33mcommit eb559655d269bcf12c50b6d5ca1b9bd20cdb54c5[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Mon Feb 20 00:28:09 2012 -0800

    finished basics of swapping a phone number for an email/password

[33mcommit ac39ff5f97d2e0a3a1f6638a3884f3d141e8c2a7[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Sun Feb 19 22:49:12 2012 -0800

    Continued work on changes

[33mcommit 29c364f8e632c64d7614781542d47166d55e569f[m
Merge: 588f28c 3aef160
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Sun Feb 19 22:33:29 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev
    
    Conflicts:
    	src/server/Client.java
    	src/server/User.java

[33mcommit 588f28c4cb6ffe889352e9353faf652eb449e8c4[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Sun Feb 19 22:29:03 2012 -0800

    Swapping phone number for email/pass/question/answer

[33mcommit 3aef160e116697708c7e9c3db02245e88587de60[m
Merge: ba94a31 ffd89b0
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sat Feb 18 15:57:45 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit ba94a31cd4f9d94a5bc601e26d70a9e8d66efd31[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sat Feb 18 15:57:25 2012 -0800

    Updated nameplates.

[33mcommit 205ca648176f7c1dc66f578424a4f6f6dd26d65a[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sat Feb 18 14:44:45 2012 -0800

    Switched from map images to nameplates.

[33mcommit ffd89b08629397a1d6952cbccdbc900c0d8ba078[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Tue Feb 14 17:10:28 2012 -0800

    Added friendlayout

[33mcommit d378880b86e634d7d3821ac22a766d2a56b376b2[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Tue Feb 14 17:09:47 2012 -0800

    Editted names to reflect change from 'social' to 'friends'

[33mcommit f72314dae66609cdb65f0da42cdb70acaab1f78a[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Tue Feb 14 01:10:31 2012 -0800

    Moved in changes from scratch to include better picking algorithm, added more geo points, removed google maps so it now just builds on target Android 2.2. Changed lat/long from ints to doubles.

[33mcommit bf54bfa74d981c088c4ae5703195e153afff0606[m
Merge: f3a50d8 76695a8
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Tue Feb 7 22:48:55 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit f3a50d899328a687326198bbc0805cfa6c12f553[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Tue Feb 7 22:48:32 2012 -0800

    Fixed pinch meeting point problem, added centering on location or a home point on map open.

[33mcommit 6824450c8d952ee30d66994c1a021d5ff8483d47[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Jan 19 00:57:50 2012 -0800

    Removed image scaling.

[33mcommit 778f4d7885aacecfd7a5a91e9c3abc7573c5243e[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Jan 19 00:39:05 2012 -0800

    Updated.

[33mcommit 76695a8b0dab8ace4dd489c8571662311aa246f4[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Wed Jan 18 21:43:03 2012 -0800

    Added user getters to client

[33mcommit 4fe1141d67ec2f9d9fa4c0b476e44927caef381f[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Wed Jan 18 15:50:07 2012 -0800

    Merged some scratched changes, added friend tracking on map.

[33mcommit 764c0a5df165b1cf0502c843da7f4e767a0adf4c[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Wed Jan 18 03:38:27 2012 -0800

    Updated client location service.

[33mcommit ebc26e155040887b29ed77094624c7e56166fe4d[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Tue Jan 17 23:44:54 2012 -0800

    Added ClientLocationUpdated event

[33mcommit bb9036a30a9022bb2b6c5c0b0869448ec3d6dc5c[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Tue Jan 17 23:22:53 2012 -0800

    Changed lat/long strings to ints in client, added ClientLocationService.java

[33mcommit 3a4fe89a7c39e15a3e9f2eff7fd3296b6525450f[m
Merge: f4dc49a b4f2abc
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Tue Jan 17 20:56:12 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit b4f2abc3fa3a84fba1ef4112878b4120bfc2fd39[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Tue Jan 17 18:17:30 2012 -0800

    Added server

[33mcommit 46b183c65b408e56913b611d3d76a50fde47972b[m
Merge: 8e204c4 db74216
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Tue Jan 17 18:14:21 2012 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev
    
    Conflicts:
    	src/com/osu/sc/meadows/GeoMapActivity.java

[33mcommit 8e204c4aae852acc4a1156f50ac1f15b7f00264e[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Tue Jan 17 18:08:51 2012 -0800

    Servery things completed

[33mcommit c2c8b0113e29847b952bd26d411c18801318ce33[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Sun Jan 15 13:46:32 2012 -0800

    Preping to branch to test AsyncTask

[33mcommit f4dc49a984bf6575720215684fcdb3ba9a0858a5[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Dec 30 17:09:34 2011 -0800

    Added a few known points from google earth to the data file.

[33mcommit db742162668ad16fe9423ee27ea846f28df8f2c8[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sat Dec 24 04:19:55 2011 -0800

    Not using kd tree, for now at least since it's harder to work with and we don't have many points yet. Sped up draws by decoding bitmaps only once.

[33mcommit 694d2b80667d317571ee12c81e36cbe41517ea1f[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sat Dec 24 01:28:56 2011 -0800

    Fixed a couple bugs, added meadows backside image.

[33mcommit 6559a86e542381cdbb96ae72411287715288a355[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Dec 23 21:02:09 2011 -0800

    Forgot to add KdTree.java

[33mcommit 6096448c4230ef396bac4a21d92d5b82315ba1e1[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Dec 23 05:05:54 2011 -0800

    Updated nearest neighbor algorithm from linear search to an open source kd tree algorithm.

[33mcommit 5f81af7a449b7139b6e1983ef0c2f13205da3793[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 22 05:05:22 2011 -0800

    Added a hack to ImageViewTouch to get single tap events between the OnSingleTapConfirmed and onLongPress gap.

[33mcommit 5b0bb3b04284219ded97859b54d9dc0829032dc0[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 22 01:41:18 2011 -0800

    Added detection for if a user or meeting is pressed on the map, and a stub for if a friend is pressed.

[33mcommit a3cb6f2d44af106afd4ad6f4630f9b3bdb58fd89[m
Merge: e12e99f d4b3d0b
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Wed Dec 21 20:45:28 2011 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev
    
    Conflicts:
    	AndroidManifest.xml

[33mcommit e12e99fc6f2e26c9d932659a627d643ec540018f[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Wed Dec 21 20:42:46 2011 -0800

    Implementing servery things

[33mcommit d4b3d0be3809e79afd0782c81700f5a6b01a51b6[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Wed Dec 21 19:27:21 2011 -0800

    Missed a bunch of files on last checkin. Also changed all integer casts in image <-> screen conversion to floating point precision/rounding to reduce icon jitter while panning.

[33mcommit 1a651867b526674bb2fb6221785c247574032dfc[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Wed Dec 21 19:12:30 2011 -0800

    Added activity for creating a meeting point on map long press. Refactored GeoMapActivity; moved some of the inner classes into a separate framework package.

[33mcommit 4f94f2c96a22af6108d6ad61055947fb701fa578[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Mon Dec 19 20:27:39 2011 -0800

    Added long press support on imagetouch.

[33mcommit e2c913ec1ee967d20eb3b2dd7935202ecda9ce3b[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sun Dec 18 18:25:29 2011 -0800

    Added saving and loading of last known location on app close. Added conversion functions for screen to image coordinates and image to screen coordinates into ImageViewTouchBase.

[33mcommit eb0beb5b402497a807e6d75ed33105d445da7a83[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sun Dec 18 01:53:54 2011 -0800

    Zoom support for geo reference placement. Removed google map view.

[33mcommit fadbcd97508d649ef8d0f90d3d5b7b0314358bdf[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sat Dec 17 18:52:20 2011 -0800

    Removed mytrackslib.

[33mcommit 46412bef6eb638f27bfc5c11846ea8ce23c95c45[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sat Dec 17 18:50:49 2011 -0800

    Revert "Committed MyTracksLib removal and some imageview stuff."
    
    This reverts commit bb94597747b57474dd5228cd6d093212b776f3e3.

[33mcommit bb94597747b57474dd5228cd6d093212b776f3e3[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Sat Dec 17 17:37:54 2011 -0800

    Committed MyTracksLib removal and some imageview stuff.

[33mcommit 4c5a25037a83a458b09e692e1891aeca82fc9bfb[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Dec 16 14:40:17 2011 -0800

    Fixed mOrigin being overridden to null.

[33mcommit 43ee48e364862d2ad666433231adb4cf46e19c0d[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Dec 16 12:06:38 2011 -0800

    Changed app name to looks less stupid

[33mcommit 3dd6422d57319a5ffc05772db693e48192e13c6d[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Dec 16 11:56:32 2011 -0800

    Cleaned up unnecessary code, no functional change

[33mcommit a825961dcc64f5cb1a4da27cfd19ad9d24d2bb2a[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Dec 16 11:43:45 2011 -0800

    Redesigned homescreen, bounded map scrolling and nifty icon

[33mcommit f95006b5209cd6b8891bbb83794a1ca8cdff6b85[m
Merge: 9301f74 f481795
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Dec 16 09:16:27 2011 -0800

    Merged geo-location with local changes

[33mcommit 9301f74297e75d60feb0f3f1a44d4a6903f6c7fd[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Fri Dec 16 09:08:08 2011 -0800

    Commented out pinch zoom and double tapping, bounding almost done

[33mcommit f481795c36d48b1a9820ce5ce711592387559c6f[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 15 19:56:01 2011 -0800

    Update.

[33mcommit 6360301c27da4f2c430277f1b2a0931fb3420e2d[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 15 01:28:53 2011 -0800

    Added map positioning.

[33mcommit 21c111d3b5fbbff8ae7bb7f3db41dde53e6b2a76[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Fri Dec 2 18:17:38 2011 -0800

    Updated to include pannable map.

[33mcommit fb963ddf970a5358979e60c37d760902dd8a3896[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 19:05:20 2011 -0800

    Added map files.

[33mcommit 6b29994429c264f5b7d2bc9a70833e247f126fd4[m
Merge: 67d4eeb c1dffe1
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 17:09:50 2011 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit 67d4eeba8a0f9e0ed1eb558204e4da7fd7503c18[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 17:09:33 2011 -0800

    Updated.

[33mcommit c1dffe1b5b9a203a3f3e4629df4f0edf4302fddd[m
Merge: df10250 007b84c
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Dec 1 16:33:31 2011 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev

[33mcommit df10250ed6e39003adf424632e77a8e7da209eff[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Dec 1 16:33:26 2011 -0800

    Commiting minor changes to strings.xml

[33mcommit 007b84c3fa883add04f8df5f330f53ce93c8b197[m
Merge: d0b14f1 9b1a024
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Thu Dec 1 16:29:44 2011 -0800

    Test

[33mcommit d0b14f12cc6efdf121afa6e8a6124c2f928a71a0[m
Author: Michael Arnold <arnolmic@onid.orst.edu>
Date:   Thu Dec 1 16:23:20 2011 -0800

    Implemented basic view for Conditions page

[33mcommit 363de7e090dde0ee40983eb15fb26fe71fbfa8a8[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 15:52:25 2011 -0800

    Updated android manifest.

[33mcommit f041ee588f7f73e7bc87fe05fc93c96dbaf0dbdf[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 15:52:06 2011 -0800

    Added meadows map activity.

[33mcommit e002fef9ece73870152a631c039a6235b75615bf[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 15:22:42 2011 -0800

    Updated.

[33mcommit 8459f499abf7f46fa7f684275ee0ebf434143aee[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 14:49:22 2011 -0800

    Updated the maplayout.xml and MeadowsMapActivity.

[33mcommit 1b4eb063b440cd705210f7572735621168ac81fb[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 14:37:57 2011 -0800

    Removed MapActivity.java

[33mcommit ac4a030b115be0e5581336b3b9494c77997585d5[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 14:37:31 2011 -0800

    Updated AndroidManifest.xml to include Google Maps.

[33mcommit 392489d935f64ce21a98b4bbfe6ac9fad947059f[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 14:37:13 2011 -0800

    Added MeadowsMapActvity.java

[33mcommit 69f8d802c2a9b9ef3e37bc9c2c24d96739178508[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 14:28:26 2011 -0800

    Updated Map and Meadows activities.

[33mcommit 9b1a024dab0212bca409bd74c32f9193473e4e72[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 14:02:43 2011 -0800

    Removed bad files.

[33mcommit 82edee41c7b8c8c17499d73e33095c028febcb71[m
Merge: 2dc077b 8b38798
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 13:57:04 2011 -0800

    Merge branch 'dev' of github.com:thetwoj/Meadows into dev
    
    Conflicts:
    	.classpath
    	.project
    	default.properties

[33mcommit 2dc077b1440e2a9bddffc8186479fc5b53f25423[m
Author: Jeff Schmitz <jschmitz28@gmail.com>
Date:   Thu Dec 1 13:56:16 2011 -0800

    message

[33mcommit 8b3879850246ae701e01181694dc0b1fe12ccc92[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Dec 1 13:17:51 2011 -0800

    Removing last generated file

[33mcommit 0d6bd38787f1ce39bf39a31da2e21a71a99099ee[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Dec 1 13:16:39 2011 -0800

    Removing generated files

[33mcommit d3671e1c57703f7cefd3fdd3999ee443c679b0f1[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Dec 1 13:04:57 2011 -0800

    Trying to establish a myTracks baseline

[33mcommit a55297697ace0cc07bd886320a2ac9fe372dc1e9[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Dec 1 12:48:20 2011 -0800

    Added more functionality to Stats page

[33mcommit 86e071465d2c0d390969ee1ee58a1c67855fa782[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Thu Nov 17 22:25:13 2011 -0800

    First rough implementation of myTracks in the StatsActivity

[33mcommit 629dd3dfa4124b89d91b850e6176aab2bd256e1e[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Wed Nov 16 13:38:04 2011 -0800

    Added comments, cleaned up code and added rough draft of menu to homescreen

[33mcommit 0afe968f3be43084b36248bce51df2da21c6ff78[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Mon Nov 14 20:30:09 2011 -0800

    Functioning first draft of homescreen

[33mcommit 31098d9108f070b80bef30839e9317af0b900623[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Wed Nov 9 21:02:25 2011 -0800

    Changed minimum SDK to 2.3 (gingerbread)

[33mcommit ee52d9b251cc13c0714eb4188bc98ad6c2c954a5[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Sat Oct 22 22:19:06 2011 -0700

    Removed Meadows_Capstone

[33mcommit 04abf48a9d49606666a4c5275d653ae6a5f1c60c[m
Merge: e30e0dd a1b8398
Author: JJ Graham <thetwoj@gmail.com>
Date:   Sat Oct 22 22:13:45 2011 -0700

    Merge branch 'master' of github.com:thetwoj/Meadows

[33mcommit e30e0ddffe4b90456b2e0ee708d65d0f9de24bec[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Sat Oct 22 22:11:50 2011 -0700

    Refactored directory to make more sense, added .gitignore for android

[33mcommit a1b8398bbf04b7eda1a15d58f60e3a5d7d70d1c1[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Sat Oct 22 21:00:02 2011 -0700

    Resolving commit errors

[33mcommit 386b3731b987d41706296cf85b850d25b26aa543[m
Author: JJ Graham <thetwoj@gmail.com>
Date:   Sun Oct 16 13:48:27 2011 -0700

    First commit - Empty Android project
