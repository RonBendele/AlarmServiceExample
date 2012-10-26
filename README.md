I use AlarmServiceExample as a test bed for Android Alarm Service.

When the app is started it checks to determine if it can set an alarm using AlarmManager.ELAPSED_REALTIME. If it cannot, the buttons related to AlarmManager.ELAPSED_REALTIME and AlarmManager.ELAPSED_REALTIME_WAKEUP are disabled.

You can set several other parameters before starting an alarm: local vs. global broadcast; type of intent to start; and the interval between alarms. I use these to understand how alarms work.