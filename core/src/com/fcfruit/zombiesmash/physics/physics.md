# Physics Info #

#### Errors ####
- If given an error similar to <b style="color:#AA0000">"AL lib: (EE) alc_cleanup: 1 device not closed"</b> or <b style="color:#AA0000">"Fatal signal 11 (SIGSEGV), code 1, fault addr 0x51e1882340 in tid 19846 (GLThread 49687)"</b>
then check where and when you are <b style="color:#00FF00">destroying bodies/joints</b> as this is typically the cause of the crash.