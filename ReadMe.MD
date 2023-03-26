### License
```
Copyright (C) 2023 Arabin

DroidPtrol 2.0 is an open source plugin for Android studio 
to do tainted data flow analysis in Application. DroidPatrol is build on top of Flowdroid.
Android developers can tyr it to find data path from source to sink.

```

<img src="https://github.com/arabin-android/DroidPatrol2/blob/master/img.png"/>

### How it works
```
DroidPatrol 2.0 is upgraded to support Kotlin code analysis.
It also supports AndroidX and Dependency Injection i.e., Dagger2, Hilt realted code analysis.
DroidPatrol is developed based on soot and jimple. We are inspired
by Flowdroid (https://github.com/secure-software-engineering/FlowDroid)
to build this plugin to perform data flow analysis.

It has 2 dependencies. android.jar and analyzer.jar both are
uploaded in this code base. Mainly analyzer.jar and android.jar
decomplies the apk and performs the analysis. To analyze the apk
as set of Source-Sink is required where all the possible source
sinks are added in a text file in this project and uploaded here.
To perform analysis developers has to pass the directory i.e c/d
drive name where the analyzer.jar and android.jar takes place.

Try DroidPatrol 2.0 and any contribution to this project is always welcome.
```

<img src="https://github.com/arabin-android/DroidPatrol2/blob/master/DroidPatrol%202.0_Architecture.png"/>

### DroidPatrol 2.0 Architecture
    
    See the architecture shown above.
```
