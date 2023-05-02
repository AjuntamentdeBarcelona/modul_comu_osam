#!/bin/bash
./gradlew build
cd ios
pod deintegrate
rm Podfile.lock
pod install