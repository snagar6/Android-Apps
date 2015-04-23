# My-Android-Apps-Framework
Author: Shreyas Nagaraj

Brief introduction:

- "Android App management app" (NOTE: More notes & slides can be found inside the project folder)
   This is an app which acts like a "settings" feature on a android device.
   The current settings feature on the android device is nothing but an app.
   And, this Admin app is pretty much a subset of that. This app has limited capabilties
   to do only a few things like:
   - Show all the Apps running on the device
   - List all the servies available on the device
   - Run basic Linux native commands (like ps, ls and cat) and display the results on screen
   - Display per app activities
   - Display per app permission listed on the manifest
   - Kill an app
   - Uninstall an app
   - Show other misc details about an app

   Note: This apps needs to be packed as a system app with special permissions which cannot
   be obtained by normal google play store apps. Ideally, OEMs would sign these system app
   along with the build package for a device. 
 
- "Micro Blogging" (Yamba)
  This App was written as a part of an Android training class by Marakana. 
  The app mimics a Twitter like app. This was an exercise in the class. 

- "Android_native_ptrace_app"
  The App would sanely work only if it can get super user privileges. 
  So, it needs to run on a rooted device with a "superuser" app running
  This is because the "ptrace" linux calls wont let a normal non-privileged linux process
  to write onto the code/data regions of another linux process. 
  This code is an extension of another app written by "JJ Ford" (copied his code which takes
  care of the shellcode execution etc"

- "New App notifier"
  This is a app which basically gets a notifation whenever an app is installed on a device
  It then queries the package manager to get all the basic info about the installed app
  and sends it accross to some other native app listening on a server socket.  

- "Log Binder Demo"
  - One more exercise done as a part of the Marakana training.
