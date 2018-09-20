# Declutter for Twitter
A decluttered tweets timeline for Android. Developed individually for the final project in the Mobile Application Development (MAD) subject at UTS.

## Requirements
- Android Studio
- Twitter Application
- [Twitter4j](http://twitter4j.org/en/) 4.0.4 or higher
- [URLImageViewHelper](https://github.com/koush/UrlImageViewHelper) 1.0.4 or higher
- Android SDK 15+ and targetted for SDK 25

## Installation

1. Setup the project and gradle in Android Studio
2. Add the JAR files for twitter4j and URLImageViewHelper in the `app/libs` directory
3. Update the Twitter helper in `app/src/main/java/com/mad/declutter/helpers/TwitterHelper.java` with your own consumer key and secret
4. Build and run the project using your phone or emulator

## Credits
- Used `CursorRecyclerAdapater` class by [Arnaud Frugier](https://quanturium.github.io/2015/04/19/using-cursors-with-the-new-recyclerview/)
