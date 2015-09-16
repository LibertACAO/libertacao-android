# Animal Liberation Cyberactivism

## What is LiberACTION?
LiberACTION is an innovative app built upon a simple concept: to revolutionize activism focused on animal liberation in Brazil through the “call to action concept”. The main goal is to promote and disclose “emergency calls” related to animal liberation, i.e actions which demand the user’s immediate intervention and interaction.

## How to setup the environment
* Download Android Studio (https://developer.android.com/intl/ko/sdk/index.html)
* Clone the project
* Open it!

### Sensitive information
There is a config file named "config.xml" located in res/values folder. You should set the values in this file to match your environment.
In addition to that, there is a special file from Fabric (crash manager), called "fabric.properties", which contains keys to access its service.
Contact someone from this project to get access to the existing data.

Flow suggestion: At terminal, type 'git update-index --assume-unchanged app/src/main/res/values/config.xml' and 'git update-index --assume-unchanged app/fabric.properties' in order to git stop monitoring changes in these files (http://stackoverflow.com/a/4498611).
After that, put the correct values, without needing to worry about not commiting these information.

## How to run
You can use an Android device, Android Studio simulator or Genymotion emulator (only for personal use - https://www.genymotion.com). Android simulator is slow, therefore we recommend the other options.

## Requirements
* Android 2.3+

## Types of events (this is more related to Parse api, but will place here while there is no common API)
1 - Feiras
2 - Vaquinhas
