# Physics Info #

#### Errors ####
- If given an error similar to <b style="color:#AA0000">"AL lib: (EE) alc_cleanup: 1 device not closed"</b>
then check where and when you are <b style="color:#00FF00">destroying bodies/joints</b> as this is typically the cause of the crash.