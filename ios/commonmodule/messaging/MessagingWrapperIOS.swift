//
// Created by Gustavo Lorena on 17/11/25.
// Copyright (c) 2025 Sergio Casero Hernández. All rights reserved.
//

import Foundation

import Foundation
import OSAMCommon
import FirebaseMessaging

class MessagingWrapperIOS: MessagingWrapper {

    func subscribeToTopic(topic: String) async throws {
        // The Firebase iOS SDK provides modern async/await functions.
        try await Messaging.messaging().subscribe(toTopic: topic)
    }

    func unsubscribeFromTopic(topic: String) async throws {
        try await Messaging.messaging().unsubscribe(fromTopic: topic)
    }
}
