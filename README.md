Remote Configuration for Android
===

[ ![Download](https://api.bintray.com/packages/guitoun3/maven/android-remote-config/images/download.svg) ](https://bintray.com/guitoun3/maven/android-remote-config/_latestVersion)


This library will parse a remote JSON to persist its key/value in SharedPreferences. If there is no Internet connection then local JSON will be used.

## Installation

### Gradle

```groovy
dependencies {
    compile 'com.github.guitoun3:remote-config:1.0.1'
}
```

## Usage

```java
new RemoteConfig(this)
        .setBaseUrl("http://yourdomain.com/")
        .setConfigFile("config.json")
        .setLocalDefaultConfigFile("default_config.json")
        .getConfig();
```

Your configuration file (config.json) must be placed on http://yourdomain.com/ and must contains key/value entries.
If when user launch your app and Internet isn't available you may want to load default configuration anyway. This is the purpose of the default_config.json file.
You have to put all default values you want to persist in SharedPreferences.

default_config.json must be placed under your assets folder.


```json
{"version":"0.9.2","ads_enabled":true,"auto_update_enable":false}
```