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
    
    lazy var osamCommons = OSAMCommons(vc: self, backendEndpoint: backendEndpoint, crashlyticsWrapper: CrashlyticsWrapperIOS(), performanceWrapper: PerformanceWrapperIOS(), analyticsWrapper: AnalyticsWrapperIOS(), platformUtil: PlatformUtilIOS())
    
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
    
    @IBAction func onDeviceInformationClick(_ sender: Any) {
        osamCommons.deviceInformation(
            f: { deviceInformationResponse, deviceInformation in
                self.showToast2(message: "deviceInformation: \(deviceInformationResponse.name), platformName: \(deviceInformation!.platformName), platformVersion: \(deviceInformation!.platformVersion), platformModel: \(deviceInformation!.platformModel)")
            }
        )
    }
    
    @IBAction func onAppInformationClick(_ sender: Any) {
        osamCommons.appInformation(
            f: { appInformationResponse, appInformation in
                self.showToast2(message: "\(appInformationResponse.name), \(appInformation!.appName), \(appInformation!.appVersionName), \(appInformation!.appVersionCode)")
            }
        )
    }
    
    @IBAction func onAppLanguageClick(_ sender: Any) {
        osamCommons.languageInformation(
            f: { appLanguageInformationResponse, languageInformation in
                self.showToast2(message: "\(appLanguageInformationResponse.name), \(languageInformation!.previousLanguage), \(languageInformation!.selectedLanguage), \(languageInformation!.displayedLanguage)")
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
        toastLabel.lineBreakMode = .byWordWrapping
        toastLabel.numberOfLines = 0
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
    
    private func showToast2(message : String, font: UIFont = .systemFont(ofSize: 12.0)) {
        let toastLabel = UILabel(frame: CGRect(x: 20, y: self.view.frame.size.height-200, width: self.view.frame.size.width-40, height: 100))
        toastLabel.backgroundColor = UIColor.black.withAlphaComponent(0.6)
        toastLabel.textColor = UIColor.white
        toastLabel.font = font
        toastLabel.textAlignment = .center;
        toastLabel.lineBreakMode = .byWordWrapping
        toastLabel.numberOfLines = 0
        toastLabel.text = message
        toastLabel.alpha = 1.0
        toastLabel.layer.cornerRadius = 10;
        toastLabel.clipsToBounds  =  true
        self.view.addSubview(toastLabel)
        UIView.animate(withDuration: 8.0, delay: 0.1, options: .curveEaseOut, animations: {
            toastLabel.alpha = 0.0
        }, completion: {(isCompleted) in
            toastLabel.removeFromSuperview()
        })
    }
    
}

