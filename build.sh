#!/bin/bash

rm -r .gradle

cd android
rm -r build
rm -r .gradle
cd ..

cd buildSrc
rm -r build
rm -r .gradle
cd ..

cd common
rm -r build
rm -r .gradle
cd ..

./gradlew build
./gradlew createFramework

cd ios
pod deintegrate
rm Podfile.lock
pod install
pod update
pod install
pod update
cd ..