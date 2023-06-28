## REACT

This is the Native benchmarking module of the thesis work. It performs the benchmarks on a given Android device. 

## Usage

This application is developed using Android Studio, Kotlin and Gradle and as such requires these to be installed. Other utilities are also necessary, such as the Android SDK among others. 

The setup necessary to run this module will most likely vary a lot depending on which platform you are on, and as such 
I refer you to the official documentation: [https://developer.android.com/studio/install](https://developer.android.com/studio/install) 

Once you have Android Studio and the SDK set up, simply open the project and install all necessary dependencies. Once this is done, you are ready to deploy the application to a device by following this guide: [https://developer.android.com/studio/run/device](https://developer.android.com/studio/run/device) 

The URL to the central storage module is hardcoded in the DataHelper.kt file. If you wish to utilize your own instance of the storage module, you'll have to change the URL there. 

The supplemental metrics such as CPU and memory utilizations need to be gathered using appropriate performance profilers
such as the Android Studio Profiler.

# BEFORE STARTING BENCHMARKS, VISIT [https://data-gatherer.onrender.com/api/data](https://data-gatherer.onrender.com/api/data) OR YOUR DEPLOYED INSTANCE AND WAIT UNTIL IT LOADS. OTHERWISE THE INSTANCE MIGHT NOT BE RUNNING AND YOU RISK DISCARDING THE RESULTS
