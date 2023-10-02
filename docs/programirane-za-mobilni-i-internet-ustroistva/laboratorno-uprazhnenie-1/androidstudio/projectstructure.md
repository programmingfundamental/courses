---
layout: default
title: Структора на проект
parent: Среда за разработване
grand_parent: Лабораторно упражнение 1
has_children: true
nav_order: 4
permalink: /docs/programirane-za-mobilni-i-internet-ustroistva/laboratorno-uprazhnenie-1/androidstudio
---
# Структора на проект

Проектите в Android Studio използва Gradle за изграждане на приложенията. В Gradle скрипта се оказват минималната и целевата версия на Android за които се разработва програмата. Зависимите библиотеки към приложението.

![](<../../../../assets/image (115).png>)

Във файла AndroidManifest.xml се задава конфигурацията на приложението. Името на приложението, главното активити, иконата и темата на дизайна, разрешения за функциите на телефона до които ще има достъп приложението.

```
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
          package="com.github.thevelislavkolesnichenko.picassoexample">

    <uses-permission android:name="android.permission.INTERNET" />

    <application
            android:allowBackup="true"
            android:icon="@mipmap/ic_launcher"
            android:label="@string/app_name"
            android:roundIcon="@mipmap/ic_launcher_round"
            android:supportsRtl="true"
            android:theme="@style/AppTheme"
            android:usesCleartextTraffic="true">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN"/>

                <category android:name="android.intent.category.LAUNCHER"/>
            </intent-filter>
        </activity>
    </application>

</manifest>
```

В папката java са клас файловете на приложението.

![](<../../../../assets/image (16).png>)

Основната папка е res. Тя съдържа ресурсите на приложението:

* drawable - съдържа изображенията за различните разделителни способности на екрана
* layout - съдържа оформлението на потребителския интерфейс
* menu - съдържа XML на менюто
* values - различни стойности като текстове, масиви и други
