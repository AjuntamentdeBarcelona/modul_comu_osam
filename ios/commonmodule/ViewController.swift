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
    
    @IBAction func onVersionControlClick(_ sender: Any) {
        VersionControl(vc: self).check(
            appId: "cat.bcn.areadum",
            versionCode: 2020021715,
            language: Language.en,
            f: {_ in }
        )
    }
    
    @IBAction func onRatingClick(_ sender: Any) {
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }


}

