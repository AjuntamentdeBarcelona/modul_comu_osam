// swift-tools-version:5.9
import PackageDescription

let package = Package(
    name: "OSAMCommon",
    platforms: [
        .iOS(.v13)
    ],
    products: [
        .library(
            name: "OSAMCommon",
            targets: ["OSAMCommon"]
        )
    ],
    targets: [
        .binaryTarget(
            name: "OSAMCommon",
            path: "./OSAMCommon.xcframework"
        )
    ]
)
