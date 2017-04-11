Sample app to reproduce issue with okhttp where network calls will hang after go
ing offline and then going back online.

# Steps to reproduce:
* __USE AN EMULATOR__ (I have tested on default Android emulator and Genymotion)
* Launch app (40 network calls will be made to saturate the connection pool)
* Put the emulator in airplane mode
* After a second or two, take the device out of airplane mode
* The app is setup to know whent the device is coming online.  When it detects t
his, it will make a network call.  This network call will now hang until it time
s out.
* Press the FAB in the bottom right corner to make another network call. This on
e will hang/fail too.  You won't be able to make another successful network call
 until a connection is taken out of the connection pool after 5 minutes of inact
ivity.

