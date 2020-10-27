//
//  ViewController.swift
//  commonmodule
//
//  Created by Sergio Casero Hernández on 26/05/2020.
//  Copyright © 2020 Sergio Casero Hernández. All rights reserved.
//

import UIKit
import common

class ViewController: UIViewController {

    @IBOutlet weak var checkVersionControl: UIButton!
    
    lazy var osam = OSAM(vc: self)
    
    @IBAction func onVersionControlClick(_ sender: Any) {
        osam.versionControl(
            appId: "cat.bcn.areadum",
            versionCode: 2020021715,
            language: Language.en,
            f: {_ in }
        )
    }
    
    @IBAction func onRatingClick(_ sender: Any) {
        osam.rating(
            appId: "cat.bcn.areadum",
            f: {_ in }
        )
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

}

