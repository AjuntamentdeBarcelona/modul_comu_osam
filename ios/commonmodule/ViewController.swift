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

class ViewController: UIViewController {
    
    @IBOutlet weak var checkVersionControl: UIButton!
    
    lazy var osamCommons = OSAMCommons(vc: self, backendEndpoint: backendEndpoint, crashlyticsWrapper: CrashlyticsWrapperIOS(), analyticsWrapper: AnalyticsWrapperIOS(), platformUtil: PlatformUtilIOS())
    
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
            f: { versionControlResponse in
                self.showToast(message: versionControlResponse.name)
            }
        )
    }
    
    @IBAction func onRatingClick(_ sender: Any) {
        osamCommons.rating(
            language: Language.es,
            f: { ratingResponse in
                self.showToast(message: ratingResponse.name)
            }
        )
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }
    
    private func showToast(message : String, font: UIFont = .systemFont(ofSize: 12.0)) {
        
        let toastLabel = UILabel(frame: CGRect(x: self.view.frame.size.width/2 - 75, y: self.view.frame.size.height-100, width: 150, height: 35))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.font = font
        toastLabel.textAlignment = .center;
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.layer.cornerRadius = 10;
        toastLabel.clipsToBounds  =  true
        self.view.addSubview(toastLabel)
        UIView.animate(withDuration: 4.0, delay: 0.1, options: .curveEaseOut, animations: {
            toastLabel.alpha = 0.0
        }, completion: {(isCompleted) in
            toastLabel.removeFromSuperview()
        })
    }
    
}

