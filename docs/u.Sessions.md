
## Sessions

A session is a real time spent in the game, it starts when the game is launched and ends when the game goes to background. But if the player returns to game in 60 seconds, game will continue in current session. Tracking of sessions produces two events, session_start and session_end. 

To track session start call `trackSessionStart()` from where whole game gets focus (e.g. ACTIVATE event) and to track session end call `trackSessionEnd()` from where whole game loses focus (e.g. DEACTIVATE event). 


Start session: 

```as3
Exponea.instance.trackSessionStart();
```


End session:

```as3
Exponea.instance.trackSessionEnd();
```
