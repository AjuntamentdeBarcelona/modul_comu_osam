# modul_comu_osam

[![](https://jitpack.io/v/AjuntamentdeBarcelona/modul_comu_osam.svg)](https://jitpack.io/#AjuntamentdeBarcelona/modul_comu_osam)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

# README

## Com es fa servir?

### Android

- Afegeix aquesta dependència en el teu projecte:

```groovy
implementation 'com.github.AjuntamentdeBarcelona.modul_comu_osam:common-android:2.1.6'
```

- Afegir aquest codi al teu **build.gradle**

```groovy
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

### iOS

- Per utilitzar el mòdul de control de versions, cal afegir l'arxiu Podfile la ubicació del
  repositori:

```
pod 'OSAMCommon', :git => 'https://github.com/AjuntamentdeBarcelona/modul_comu_osam.git', :tag => '2.1.6'
```

- Actualitzar mitjançant el comandament `pod update` les dependències.

## Introducció

Aquest mòdul uneix el control de versions i el control de valoracions.

En el control de versions es mostrarà un avís quan el servei avisi que hi ha una nova versió de
l'app. Aquesta alerta la podem mostrar amb un missatge amb botons de confirmació d'accions.

Tindrem tres diferents tipus d'alerta:

1. **Informativa**: Alerta amb un missatge i / o un títol informatiu, amb un botó d ' "ok" per
   confirmar que s'ha llegit.
2. **Restrictiva**: Alerta amb un missatge i / o un títol, amb botó d ' "ok" que un cop fet clic
   redirigirà l'usuari a una url.
3. **Permisiva**: Alerta amb un missatge i / o un títol, amb botons de "ok" i "cancel". Si fem clic
   al botó de cancel·lar l'alerta desapareixerà, i si ho fem al de confirmar s'obrirà una url.

Pel que respecta al control de valoracions, la seva funcionalitat és mostrar periòdicament una popup
que convida a l’usuari a deixar un comentari sobre l'app al market place corresponent (Google Play o
AppStore). 

A Android s'utilitza la [llibreria de Google Play Core](https://developer.android.com/guide/playcore/in-app-review/kotlin-java)

A iOS s'utilitza la llibreria nativa:

```kotlin
SKStoreReviewController.requestReview()
```

## Descàrrega del mòdul

Des de la OSAM es proporcionen mòduls per realitzar un conjunt de tasques comunes a totes les apps
publicades per l'Ajuntament de Barcelona.

El mòdul comú (iOS / Android) està disponible com a repositori a:
[https://github.com/AjuntamentdeBarcelona/modul_comu_osam](https://github.com/AjuntamentdeBarcelona/modul_comu_osam)

## Configuració del mòdul

Per tal de poder utilitzar el mòdul, és necessari especificar en temps de inicialització els
següents paràmetres:

- **backendEndpoint**: url del backend del mòdul comú
- **crashlyticsWrapper**: implementació del wrapper de crashlytics que hem de implementar (o de
  qualsevol altre llibreria)
- **analyticsWrapper**: implementació del wrapper de analytics que hem de implementar (o de
  qualsevol altre llibreria)
- **performanceWrapper**: implementació del wrapper de performance que hem de implementar (o de
  qualsevol altre llibreria)

A continuació, es detalla per cada plataforma, com es realitza aquesta inicialització. Per a més
detalls de com integrar el mòdul comú amb la CI de la OSAM, consultar el manual de la CI.

### Android

Inicialitzarem el mòdul comú de la següent manera:

```kotlin
private val osamCommons by lazy {
    OSAMCommons(
        activity = this,
        context = this,
        backendEndpoint = getString(R.string.common_module_endpoint),
        crashlyticsWrapper = CrashlyticsWrapperAndroid(),
        performanceWrapper = PerformanceWrapperAndroid(),
        analyticsWrapper = AnalyticsWrapperAndroid(this),
        platformUtil = PlatformUtilAndroid(this)
    )
}
```
La URL del backend s'ha de declarar en el config_keys.xml amb el nom "common_module_endpoint". El fixer quedaria de la següent manera:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="common_module_endpoint" translatable="false">https://dev-osam-modul-comu.dtibcn.cat/</string>
</resources>
```

A continuació s'indiquen les implementacions del wrapper de crashlytics, analytics, performance i platform util:

```kotlin
class CrashlyticsWrapperAndroid : CrashlyticsWrapper {
  override fun recordException(exception: Exception) {
    FirebaseCrashlytics.getInstance().recordException(exception)
  }
}

class AnalyticsWrapperAndroid(context: Context) : AnalyticsWrapper {

  private val analytics = FirebaseAnalytics.getInstance(context)

  override fun logEvent(name: String, parameters: Map<String, String>) {
    analytics.logEvent(name, parameters.toBundle())
  }

  private fun Map<String, String>.toBundle(): Bundle =
    Bundle().apply {
      this@toBundle.forEach {
        putString(it.key, it.value)
      }
    }
}

class PerformanceWrapperAndroid : PerformanceWrapper {
  override fun createMetric(url: String, httpMethod: String): PerformanceMetric {
    return PerformanceMetricAndroid(FirebasePerformance.getInstance().newHttpMetric(url, httpMethod))
  }
}

class PerformanceMetricAndroid(val metric: HttpMetric?) : PerformanceMetric {
    override fun start() {
        metric?.start()
    }

    override fun setRequestPayloadSize(bytes: Long) {
        metric?.setRequestPayloadSize(bytes)
    }

    override fun markRequestComplete() {
        metric?.markRequestComplete()
    }

    override fun markResponseStart() {
        metric?.markResponseStart()
    }

    override fun setResponseContentType(contentType: String) {
        metric?.setResponseContentType(contentType)
    }

    override fun setHttpResponseCode(responseCode: Int) {
        metric?.setHttpResponseCode(responseCode)
    }

    override fun setResponsePayloadSize(bytes: Long) {
        metric?.setResponsePayloadSize(bytes)
    }

    override fun putAttribute(attribute: String, value: String) {
        metric?.putAttribute(attribute, value)
    }

    override fun stop() {
        metric?.stop()
    }
}

class PlatformUtilAndroid(private val context: Context) : PlatformUtil {
  override fun encodeUrl(url: String): String? {
    return url
  }

  override fun openUrl(url: String): Boolean {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.data = Uri.parse(url)
    ContextCompat.startActivity(context, intent, null)
    return true
  }

  override fun getDeviceModelIdentifier(): String {
    return ""
  }
}
```

### iOS

Inicialitzarem el mòdul comú de la següent manera:

```swift
lazy var osamCommons = OSAMCommons(
    vc: self,
    backendEndpoint: <url_endpoint_modul_comu>,
    crashlyticsWrapper: CrashlyticsWrapperIOS(),
    performanceWrapper: PerformanceWrapperIOS(),
    analyticsWrapper: AnalyticsWrapperIOS(),
    platformUtil: PlatformUtilIOS()
  )
```

La URL del backend s'ha de declarar en el config_keys.plist amb el nom "common_module_endpoint". El fixer quedaria de la següent manera:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE plist PUBLIC "-//Apple//DTD PLIST 1.0//EN" "http://www.apple.com/DTDs/PropertyList-1.0.dtd">
<plist version="1.0">
<dict>
  <key>common_module_endpoint</key>
  <string>https://dev-osam-modul-comu.dtibcn.cat/</string>
</dict>
</plist>
```

A continuació s'indiquen les implementacions del wrapper de Crashlytics, Performance i Analytics:

```swift
class CrashlyticsWrapperIOS: CrashlyticsWrapper {
    func recordException(className: String, stackTrace: String) {
        let exception = ExceptionModel(name: className, reason: stackTrace)
        Crashlytics.crashlytics().record(exceptionModel: exception)
    }
}

class PerformanceWrapperIOS: PerformanceWrapper {
    func createMetric(url: String, httpMethod: String) -> PerformanceMetric {
        
        let httpMethodType: HTTPMethod
        
        switch httpMethod.lowercased() {
        case "put":
            httpMethodType = HTTPMethod.put
        case "post":
            httpMethodType = HTTPMethod.post
        case "delete":
            httpMethodType = HTTPMethod.delete
        case "head":
            httpMethodType = HTTPMethod.head
        case "patch":
            httpMethodType = HTTPMethod.patch
        case "options":
            httpMethodType = HTTPMethod.options
        case "trace":
            httpMethodType = HTTPMethod.trace
        case "connect":
            httpMethodType = HTTPMethod.connect
        default:
            httpMethodType = HTTPMethod.get
        }
        
        return PerformanceMetricIOS(
            metric: HTTPMetric.init(url: URL(string: url)!, httpMethod: httpMethodType)!
        )
    }
}

class PerformanceMetricIOS: PerformanceMetric {
    
    let metric: HTTPMetric?
    
    init(metric: HTTPMetric?) {
        self.metric = metric
    }
    
    func start() {
        metric?.start()
    }
    func setRequestPayloadSize(bytes: Int64) {
        metric?.requestPayloadSize = Int(bytes)
    }
    func markRequestComplete() {
        // not used for iOs
    }
    func markResponseStart() {
        // not used for iOs
    }
    func setResponseContentType(contentType: String) {
        metric?.responseContentType = contentType
    }
    func setHttpResponseCode(responseCode: Int32) {
        metric?.responseCode = Int(responseCode)
    }
    func setResponsePayloadSize(bytes: Int64) {
        metric?.responsePayloadSize = Int(bytes)
    }
    func putAttribute(attribute: String, value: String) {
        metric?.setValue(value, forAttribute: attribute)
    }
    func stop() {
        metric?.stop()
    }
}

class AnalyticsWrapperIOS: AnalyticsWrapper {
    func logEvent(name: String, parameters: [String : String]) {
        Analytics.logEvent(name, parameters: parameters)
    }
}

class PlatformUtilIOS : PlatformUtil {
    func encodeUrl(url: String) -> String? {
        let urlString: String? = url.addingPercentEncoding(withAllowedCharacters: .urlFragmentAllowed)
        return urlString
    }
    
    func openUrl(url: String) -> Bool {
        if let urlObj = URL(string: url) {
            UIApplication.shared.open(urlObj)
            return true
        } else {
            return false
        }
    }
    
    func getDeviceModelIdentifier() -> String {
        var modelName: String {
            var systemInfo = utsname()
            uname(&systemInfo)
            let machineMirror = Mirror(reflecting: systemInfo.machine)
            let identifier = machineMirror.children.reduce("") { identifier, element in
                guard let value = element.value as? Int8, value != 0 else { return identifier }
                return identifier + String(UnicodeScalar(UInt8(value)))
            }
            
            switch identifier {
            /*case "iPhone11,6": return "iPhone XR"
            case "iPhone11,4", "iPhone11,2": return "iPhone XS Max"
            case "iPhone11,8": return "iPhone XS"
            case "iPhone12,1": return "iPhone 11"
            case "iPhone12,3": return "iPhone 11 Pro"
            case "iPhone12,5": return "iPhone 11 Pro Max"*/
            //Add more cases for other devices as needed
            default: return identifier
            }
        }
        return modelName
    }
}
```

## Implementació control de versions

### Android

Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les
variables ja definides i mostrarà l'alerta segons els valors rebuts:

```kotlin
osamCommons.versionControl(
    language = Language.CA
) {
    // Do something...
}
```

A la inicialització se li ha de passar el context de l'app. Per cridar al control de versions només
cal executar la funció `versionControl()` i facilitar-li l'idioma en què es vol mostrar el popup.
Per facilitar l'idioma, la llibreria inclou la classe `Language` que conté **Català (CA)**,
**Castellà (ES)** i **Anglès (EN)**, que són els idiomes suportats. Com a extra, se li ha afegit un
callback perquè la pantalla principal pugui reaccionar en cas que hi hagi hagut un error o si, a
part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de
l'aplicació. Lo que revem en el callback es l'objecte `VersionControlResponse`. Aquest objecte pot
arrivar amb quatre valors possibles:

- **ACCEPTED**: si l'usuari ha escollit el botó d'acceptar/ok
- **DISMISSED**: si l'usuari ha tret el popup
- **CANCELLED**: si l'usuari ha escollit el botó de cancel·lar
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el
  popup

Per exemple: Si l'usuari cancel·la el popup, al callback rebriem
l'objecte `VersionControlResponse.CANCELLED`. Si en el cas de que volgués fer alguna acció diferent
si l'usuari cancel·la el popup, es podria definir en aquest punt la casuistica.

### iOS

Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les
variables ja definides i mostrarà l'alerta segons els valors rebuts:

```swift
osamCommons.versionControl(
  language: Language.es,
  f: {_ in }
)
```

A la inicialització se li ha de passar el UIViewController de la pantalla que crida al mòdul. Per
cridar al control de versions només cal executar la funció `versionControl()`, facilitar-li l'idioma
en què es vol mostrar el popup i la funció que volem que executi el callback que retorna el mòdul.
Per facilitar l'idioma, la llibreria inclou la classe `Language` que conté **Català (CA)**,
**Castellà (ES)** i **Anglès (EN)**, que són els idiomes suportats. Pel que respecta al callback,
s'ha afegit perquè la pantalla pugui reaccionar en cas que hi hagi hagut un error o si, a part de la
funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de
l'aplicació. Lo que revem en el callback es l'objecte `VersionControlResponse`. Aquest objecte pot
arrivar amb quatre valors possibles:

- **ACCEPTED**: si l'usuari ha escollit el botó d'acceptar/ok
- **DISMISSED**: si l'usuari ha tret el popup o no compleix les condicions per mostrar-li a l'usuari
- **CANCELLED**: si l'usuari ha escollit el botó de cancel·lar
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el
  popup

Per exemple: Si l'usuari cancel·la el popup, al callback rebriem
l'objecte `VersionControlResponse.CANCELLED`. Si en el cas de que volgués fer alguna acció diferent
si l'usuari cancel·la el popup, es podria definir en aquest punt la casuistica.

## Implementació control de valoracions

### Android

Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les
variables ja definides i mostrarà l'alerta segons els valors rebuts:

```kotlin
osamCommons.rating(
  language = Language.CA
) {
  // Do something...
}
```

A la inicialització se li ha de passar el context de l'app. Per cridar al control de valoracions
només cal executar la funció `rating()` i facilitar-li l'idioma en què es vol mostrar el popup. Per
facilitar l'idioma, la llibreria inclou la classe `Language` que conté **Català (CA)**,
**Castellà (ES)** i **Anglès (EN)**, que són els idiomes suportats. Com a extra, se li ha afegit un
callback perquè la pantalla principal pugui reaccionar en cas que hi hagi hagut un error o si, a
part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de
l'aplicació. Lo que revem en el callback es l'objecte `RatingControlResponse`. Aquest objecte pot
arrivar amb quatre valors possibles:

- **ACCEPTED**: s'ha sol·licitat que surti el popup natiu de valoració de Android: Google In-App Review
- **DISMISSED**: el popup no compleix les condicions per mostrar-li a l'usuari
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el
  popup

Per exemple: Si l'usuari treu el popup, al callback rebriem
l'objecte `RatingControlResponse.DISMISSED`. Si en el cas de que volgués fer alguna acció diferent
si l'usuari ha tret el popup o aquest no compleix les condicions per sortir, es podria definir en
aquest punt la casuistica.

### iOS

Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les
variables ja definides i mostrarà l'alerta segons els valors rebuts:

```swift
osamCommons.rating(
  language: Language.es,
  f: {_ in }
)
```

A la inicialització se li ha de passar el UIViewController de la pantalla que crida al mòdul. Per
cridar al control de valoracions només cal executar la funció `rating()`, facilitar-li l'idioma en
què es vol mostrar el popup i la funció que volem que executi el callback que retorna el mòdul. Per
facilitar l'idioma, la llibreria inclou la classe `Language` que conté **Català (CA)**,
**Castellà (ES)** i **Anglès (EN)**, que són els idiomes suportats. Pel que respecta al callback,
s'ha afegit perquè la pantalla pugui reaccionar en cas que hi hagi hagut un error o si, a part de la
funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de
l'aplicació. Lo que revem en el callback es l'objecte `RatingControlResponse`. Aquest objecte pot
arrivar amb tres valors possibles:

- **ACCEPTED**: s'ha sol·licitat que surti el popup natiu de valoració d'iOS:
  SKStoreReviewController
- **DISMISSED**: el popup no compleix les condicions per mostrar-li a l'usuari
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el
  popup

Per exemple: Si l'usuari treu el popup, al callback rebriem
l'objecte `RatingControlResponse.DISMISSED`. Si en el cas de que volgués fer alguna acció diferent
si l'usuari ha tret el popup o aquest no compleix les condicions per sortir, es podria definir en
aquest punt la casuistica.

## Implementació per obtenir informació de la plataforma

### Android

Entre la informació a extreure està: informació del model , sistema operatiu, versió de SO, nom app,
i versió de l'app.

```kotlin
osamCommons.deviceInformation { deviceInformationResponse, deviceInformation ->
  // Do something...
}
```

Per obtenir l'informació només cal executar la funció `deviceInformation()`. Com a extra, se li ha afegit un
callback perquè la pantalla principal pugui reaccionar en cas que hi hagi hagut un error o si, a
part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de
l'aplicació. Lo que revem en el callback es l'objecte `DeviceInformationResponse`. Aquest objecte pot
arrivar amb 2 valors possibles:

- **ACCEPTED**: s'ha pogut obtenir les dades i s'han retornat correctament
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria

### iOS

Entre la informació a extreure està: informació del model , sistema operatiu, versió de SO, nom app,
i versió de l'app.

```swift
osamCommons.deviceInformation(
  f: {deviceInformationResponse, deviceInformation in }
)
```

Per obtenir l'informació només cal executar la funció `deviceInformation()`. Com a extra, se li ha afegit un
callback perquè la pantalla principal pugui reaccionar en cas que hi hagi hagut un error o si, a
part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de
l'aplicació. Lo que revem en el callback es l'objecte `DeviceInformationResponse`. Aquest objecte pot
arrivar amb 2 valors possibles:

- **ACCEPTED**: s'ha pogut obtenir les dades i s'han retornat correctament
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria

## Implementació per obtenir informació de l'app

### Android

Entre la informació a extreure està: informació del model , sistema operatiu, versió de SO, nom app,
i versió de l'app.

```kotlin
osamCommons.appInformation { appInformationResponse, appInformation ->
  // Do something...
}
```

Per obtenir l'informació només cal executar la funció `appInformation()`. Com a extra, se li ha afegit un
callback perquè la pantalla principal pugui reaccionar en cas que hi hagi hagut un error o si, a
part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de
l'aplicació. Lo que revem en el callback es l'objecte `AppInformationResponse`. Aquest objecte pot
arrivar amb 2 valors possibles:

- **ACCEPTED**: s'ha pogut obtenir les dades i s'han retornat correctament
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria

### iOS

Entre la informació a extreure està: informació del model , sistema operatiu, versió de SO, nom app,
i versió de l'app.

```swift
osamCommons.appInformation(
  f: { appInformationResponse, appInformation in }
)
```

Per obtenir l'informació només cal executar la funció `appInformation()`. Com a extra, se li ha afegit un
callback perquè la pantalla principal pugui reaccionar en cas que hi hagi hagut un error o si, a
part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de
l'aplicació. Lo que revem en el callback es l'objecte `AppInformationResponse`. Aquest objecte pot
arrivar amb 2 valors possibles:

- **ACCEPTED**: s'ha pogut obtenir les dades i s'han retornat correctament
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria

## Format JSONs

### Control de Versions

```json
{
  "data": {
    "id": 109,
    "appId": 400,
    "packageName": "cat.bcn.commonmodule",
    "versionCode": 2021050000,
    "versionName": "1.0.0",
    "startDate": 1645311600000,
    "endDate": 1645311600000,
    "serverDate": 1645788600000,
    "platform": "IOS",
    "comparisonMode": "NONE",
    "title": {
      "es": "TITLE_ES",
      "en": "TITLE_EN",
      "ca": "TITLE_CA"
    },
    "message": {
      "es": "MESSAGE_ES",
      "en": "MESSAGE_EN",
      "ca": "MESSAGE_CA"
    },
    "ok": {
      "es": "OK",
      "en": "OK",
      "ca": "OK"
    },
    "cancel": {
      "es": "Cancelar",
      "en": "Cancel",
      "ca": "Cancel.lar"
    },
    "url": "https://apps.apple.com/es/app/barcelona-a-la-butxaca/id1465234509?l=ca"
  }
}
```

#### Paràmetres

- **packageName**
    - Obligatori
    - Especifica el ApplicationID o BundleID de l'app que afecta
- **versionCode**
    - Obligatori
    - Especifica la versió a la que afecta el control de versions
- **startDate**
    - Opcional
    - Data des de quan s'ha de començar a mostrar el pop-up del control de versions, expressada
      amb *timestamp* (milisegons des del 01/01/1970). Si no arriba informada, es considerarà com si
      fos el 0.
- **endDate**
    - Opcional
    - Data fins quan s'ha de mostrar el pop-up del control de versions, expressada amb *timestamp* (
      milisegons des del 01/01/1970). Si no arriba informada, es considerara com si fos
      9223372036854775807 (el valor màxim possible del Long).
- **serverDate**
    - Obligatori
    - Data actual proporcionada per el servidor. Serà la que s'utilitzi per comparar amb `startDate`
      y `endDate`.
- **platform**
    - Obligatori
    - Especifica per a quina plataforma (ANDROID o IOS) afecta
- **comparisonMode**
    - Obligatori
    - Especifica la manera de comparació de la versió de l'app amb el mòdul
- **title**
    - Obligatori
    - Títol de l'alerta en el cas que s'hagi de mostrar.
- **message**
    - Obligatori
    - Missatge de l'alerta en cas que s'hagi de mostrar.
- **ok**
    - Opcional
    - Títol del botó d'acceptar.
    - Si es rep aquest paràmetre juntament amb el paràmetre okButtonActionURL, es mostrarà en
      l'alerta un botó d'acceptar que obrirà el link que s'ha especificat en el paràmetre
      okButtonActionURL.
- **cancel**
    - Opcional
    - Títol del botó de cancel·lar
- **url**
    - Opcional
    - Link que s'obrirà quan l'usuari seleccioni el botó d'acceptar. Per exemple: link de la nova
      versió de l'aplicació a l'App Store / Google Play.

### Control de Valoracions

```json
{
  "data": {
    "id": 74,
    "appId": 401,
    "appStoreIdentifier": "1234567890",
    "packageName": "cat.bcn.commonmodule",
    "platform": "ANDROID",
    "minutes": 2880,
    "numAperture": 5,
    "message": {
      "es": "MESSAGE_ES",
      "en": "MESSAGE_EN",
      "ca": "MESSAGE_CA"
    }
  }
}
```

#### Paràmetres

- **appStoreIdentifier**
    - Obligatori
    - Especifica el id de l'app al AppStore per poder valorar-la
- **packageName**
    - Obligatori
    - Especifica el ApplicationID o BundleID de l'app que afecta
- **platform**
    - Obligatori
    - Especifica per a quina plataforma (ANDROID o IOS) afecta
- **minutes**
    - Obligatori
    - Especifica el temps (en minuts) que ha de passar perquè surti el popup
- **numAperture**
    - Obligatori
    - Especifica la quantitat de vegades que s'ha d'obrir l'app perquè surti el popup
- **message**
    - Obsolet
    - A partir de la versió 2.0.0, aquest paràmetre ja no es fa servir

## Com funciona el mòdul de control de versions

En primer lloc, ha de cumplir-se que el valor del paràmetre `serverDate` estigui entre el valor
de `startDate` i el de `endDate`. Si això no es compleix, no es mostrarà l'alerta. Si es compleix,
en funció del valor del paràmetre `comparisonMode` es mostrarà o no l'alerta. Aquest paràmetre
compararà la versió instal·lada amb la qual rebem del json, en funció de tres valors:

- **FORCE**: Mostra l'alerta i no es pot treure. Actualització obligatoria
- **LAZY**: Mostra l'alerta amb l'opció d'actualitzar l'app o seguir utilitzant l'actual.
  Actualització voluntaria
- **INFO**: Mostra l'alerta amb un missatge informatiu. Deixa seguir utilitzant l'app amb normalitat
- **NONE**: no es mostra el popup

## Com funciona el control de valoracions

- L’app compta cada vegada que s’obre (s'ha de cridar el mètode "rating" de la llibreria)
- L’app espera a que passin un nº de minuts determinats (p.ex. 90) des de l’últim cop que ha mostrat
  la pop up (per tal de l’usuari no la consideri intrusiva o abusiva).
- Un cop passats aquests dies i quan el comptador superi un valor determinat (p.ex. 20), mostra el
  popup i el comptador es reinicia independentment de la resposta de l’usuari.*
- La operativa no es veu modificada si hi ha un canvi de versió (és a dir, es mantenen els valors de
  comptatge de dies i de nº de apertures).
- En cas de què s'hagi de mostrar el popup, a Android es crida a la llibreria de Google Play Core i a iOS es crida al SKStoreReviewController.