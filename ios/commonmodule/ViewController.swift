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
            appId: "cat.bcn.festamerce",
            versionCode: 2019092218,
            language: Language.es,
            f: {_ in }
        )
    }
    
    @IBAction func onRatingClick(_ sender: Any) {
        osam.rating(
            appId: "cat.bcn.festamerce",
            language: Language.es,
            f: {_ in }
        )
    }
    
    override func viewDidLoad() {
        super.viewDidLoad()
    }

}

