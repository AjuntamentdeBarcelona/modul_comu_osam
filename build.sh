#!/bin/bash
set -e

rm -rf .gradle

cd android
rm -rf build
rm -rf .gradle
cd ..

cd buildSrc
rm -rf build
rm -rf .gradle
cd ..

cd common
rm -rf build
rm -rf .gradle
cd ..

./gradlew build
./gradlew createFramework

echo "Verifying iOS demo project compilation with Swift Package Manager..."
xcodebuild -project ios/commonmodule.xcodeproj -scheme commonmodule -destination 'generic/platform=iOS Simulator' build CODE_SIGNING_ALLOWED=NO