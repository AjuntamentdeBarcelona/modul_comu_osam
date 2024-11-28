Pod::Spec.new do |s|
    s.name                     = 'OSAMCommon'
    s.version                  = '2.2.1'
    s.summary                  = 'Common library for the osam version and rating app control'
    s.homepage                 = 'https://github.com/AjuntamentdeBarcelona/modul_comu_osam'
    s.license                  = 'BSD'
    s.authors                  = 'Eduard Carbonell eduard.carbonell@worldline.com'
    s.vendored_frameworks      = "OSAMCommon.xcframework"
    s.source                   = { :git => "https://github.com/AjuntamentdeBarcelona/modul_comu_osam.git", :tag => "#{s.version}" }
    s.exclude_files = "Classes/Exclude"
end
