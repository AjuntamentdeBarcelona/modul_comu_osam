//
//  ViewController.swift
//  commonmodule
//
//  Created by Sergio Casero Hernández on 26/05/2020.
//  Copyright © 2020 Sergio Casero Hernández. All rights reserved.
//

import UIKit
import OSAMCommon
import StoreKit
import FirebaseAnalytics

class ViewController: UIViewController {

    @IBOutlet weak var checkVersionControl: UIButton!
    
    lazy var osamCommons = OSAMCommons(vc: self, backendEndpoint: backendEndpoint)
    
    private var backendEndpoint: String {
      get {
        guard let filePath = Bundle.main.path(forResource: "config_keys", ofType: "plist") else {
          fatalError("Couldn't find file 'config_keys.plist'.")
        }
        let plist = NSDictionary(contentsOfFile: filePath)
        guard let value = plist?.object(forKey: "common_module_endpoint") as? String else {
          fatalError("Couldn't find key 'common_module_endpoint' in 'config_keys.plist'.")
        }
        return value
      }
    }
    
    @IBAction func onVersionControlClick(_ sender: Any) {
        osamCommons.versionControl(
            language: Language.es,
            f: {_ in }
        )
    }
    
    @IBAction func onRatingClick(_ sender: Any) {
        osamCommons.rating(
            language: Language.es,
            f: {_ in }
        )
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

}

