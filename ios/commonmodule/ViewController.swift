//
//  ViewController.swift
//  commonmodule
//
//  Created by Sergio Casero Hernández on 26/05/2020.
//  Copyright © 2020 Sergio Casero Hernández. All rights reserved.
//

import UIKit
import common
import StoreKit
import FirebaseAnalytics

class ViewController: UIViewController {

    @IBOutlet weak var checkVersionControl: UIButton!
    
    lazy var osamCommons = OSAMCommons(vc: self)
    
    @IBAction func onVersionControlClick(_ sender: Any) {
        SKStoreReviewController.requestReview()
        osamCommons.versionControl(
            appId: "cat.bcn.areadum",
            versionCode: 2020021715,
            language: Language.es,
            f: {_ in }
        )
    }
    
    @IBAction func onRatingClick(_ sender: Any) {
        Analytics.logEvent("rating_popup_showed", parameters: [
            "message": "Rating popup showed"
        ])
        SKStoreReviewController.requestReview()
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

}

