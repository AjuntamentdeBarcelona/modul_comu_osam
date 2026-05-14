# Modul comú OSAM Flutter

[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

## Historial de versions

### 3.2.0
- **Control de versions avançat**: S'han afegit camps per filtrar per versió del sistema operatiu (`osVersion`) i per model de dispositiu (`models`).
- **Noves funcionalitats**: S'ha afegit el mètode `isOnline` per comprovar la connectivitat amb el backend de manera asíncrona.
- **Personalització de UI**: Els mètodes `versionControl` i `rating` ara accepten els paràmetres `isDarkMode` i `applyComModStyles` per a una millor integració visual.
- **Millores en analítica**: S'ha implementat el nou event `language_change` i s'ha millorat el registre d'events inicials.
- **Gestió del context**: S'ha afegit el mètode `setActivity` per a Android per gestionar millor els canvis de context i cicle de vida.
- **Timeout**: Implementació de missatges d'error per timeout quan el backend no respon.

### 3.1.0
- **FCM Token**: S'ha afegit el suport per obtenir el token de Firebase Cloud Messaging (FCM) mitjançant el mètode `getFCMToken`.
- **FirstTimeOrUpdate**: Aquest mètode s'encarrega de subscriure's automàticament al tòpic de la versió en l'idioma de l'aplicació si és la primera vegada que s'obre, o d'actualitzar-lo si la versió ha canviat.
- **Subscribe and unsubscribe from topic**: 2 mètodes que ajuden amb les subscripcions i cancel·lacions de subscripcions a topics d'idioma i versió.
- **Millores d'UI i accessibilitat al popup de control de versions**: S'ha millorat l'accessibilitat al popup del control de versions, incrementat el contrast dels seus elements i assegurat diferents tipus de navegació accessible.

### 3.0.0
- **Gestió de Topics**: S'han implementat els mètodes `firstTimeOrUpdateEvent`, `subscribeToCustomTopic` i `unsubscribeToCustomTopic` per a una gestió completa de les notificacions push.
- **Event de canvi d'idioma**: S'ha afegit `changeLanguageEvent` per orquestrar el canvi d'idioma, analítica i topics.
- **Migració a KMP**: Refactorització important per utilitzar Kotlin Multiplatform de manera més eficient i unificar la lògica de negoci.

### 2.3.0-dev
- **Millores de robustesa**: S'ha afegit la funció `Language.parse` per gestionar millor els idiomes per defecte i evitar excepcions.
- **Mode No Mostrar Més**: S'ha afegit la funcionalitat per permetre a l'usuari no tornar a veure el diàleg de control de versions (`checkBoxDontShowAgain`).
- **Temps d'acceptar en control de versions**: Quan al popup del control de versions es prem a acceptar en info o lazy mode el popup no tornarà a aparèixer en una quantitat de temps que sigui definida.

### 2.1.x / 2.2.x
- **Informació del sistema**: Introducció dels mètodes `deviceInformation` i `appInformation` per obtenir dades del dispositiu i de l'aplicació.
- **Refactorització de respostes**: S'ha simplificat `RatingControlResponse` (reduint els estats a `ACCEPTED`, `DISMISSED`, `ERROR`).
- **Nadiu**: Canvi a implementacions HTTP nadiues (Ktor) en lloc de CIO per a una millor estabilitat en totes les plataformes.

# modul_comu_osam

[![Jitpack version](https://jitpack.io/v/AjuntamentdeBarcelona/modul_comu_osam.svg)](https://jitpack.io/#AjuntamentdeBarcelona/modul_comu_osam)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)

## Com es fa servir?

### Android

- Afegeix aquesta dependència en el teu projecte:

```groovy
implementation 'com.github.AjuntamentdeBarcelona.modul_comu_osam:common-android:3.2.0'
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

```pod
pod 'OSAMCommon', :git => 'https://github.com/AjuntamentdeBarcelona/modul_comu_osam.git', :tag => '3.2.0'
```

- Actualitzar mitjançant el comandament `pod update` les dependències.

## Introducció

Aquest mòdul uneix el control de versions i el control de valoracions.

En el control de versions es mostrarà un avís quan el servei avisi que hi ha una nova versió de
l'app. Aquesta alerta la podem mostrar amb un missatge amb botons de confirmació d'accions.

Tindrem tres diferents tipus d'alerta:

1. **Informativa**: Alerta amb un missatge i / o un títol informatiu, amb un botó d ' "ok" per
   confirmar que s'ha llegit. No obre cap enllaç.
2. **Restrictiva**: Alerta amb un missatge i / o un títol, amb botó d ' "ok" que un cop fet clic
   redirigirà l'usuari a una url.
3. **Permisiva**: Alerta amb un missatge i / o un títol, amb botons de "ok" i "cancel". Si fem clic al botó de cancel·lar l'alerta desapareixerà, i si ho fem al de confirmar s'obrirà una url.

Pel que respecta al control de valoracions, la seva funcionalitat és mostrar periòdicament una popup
que convida a l’usuari a deixar un comentari sobre l'app al market place corresponent (Google Play o
AppStore).

A Android s'utilitza la [llibreria de Google Play Core](https://developer.android.com/guide/playcore/in-app-review/kotlin-java)

A iOS s'utilitza la llibreria nativa:

```swift
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
- **messagingWrapper**: implementació del wrapper de messaging que hem de implementar (o de
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
        platformUtil = PlatformUtilAndroid(this),
        messagingWrapper = MessagingWrapperAndroid()
    )
}
```

### Gestió del context de l'activitat (Android)

Com que `OSAMCommons` pot mostrar diàlegs (per al control de versions o la valoració de l'app), necessita una referència a l'`Activity` actual. Si la vostra aplicació té múltiples activitats o es recreen (per exemple, canvis de configuració), heu d'actualitzar la referència de l'activitat a `OSAMCommons` per assegurar-vos que els diàlegs es mostrin correctament.

Utilitzeu el mètode `setActivity` en el `onResume` de la vostra activitat:

```kotlin
override fun onResume() {
    super.onResume()
    osamCommons.setActivity(this)
}
```

La URL del backend s'ha de declarar en el config_keys.xml amb el nom "common_module_endpoint". El fixer quedaria de la següent manera:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <string name="common_module_endpoint" translatable="false">https://dev-osam-modul-comu.dtibcn.cat/</string>
</resources>
```

Per accedir a aquesta url durant el desenvolupament es requereix indicar usuari i contrasenya, informació que es pot obtenir consultant amb el Cap de Projecte de l'OSAM asignat al projecte.

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

class MessagingWrapperAndroid : MessagingWrapper {

    private val firebaseMessaging = FirebaseMessaging.getInstance()

    override suspend fun subscribeToTopic(topic: String) {
        firebaseMessaging.subscribeToTopic(topic)
    }

    override suspend fun unsubscribeFromTopic(topic: String) {
        firebaseMessaging.unsubscribeFromTopic(topic)
    }

    override suspend fun getToken(): String {
        return firebaseMessaging.token.await()
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
    platformUtil: PlatformUtilIOS(),
    messagingWrapper: MessagingWrapperIOS()
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

class MessagingWrapperIOS: MessagingWrapper {

    func subscribeToTopic(topic: String) async throws {
        // The Firebase iOS SDK provides modern async/await functions.
        try await Messaging.messaging().subscribe(toTopic: topic)
    }

    func unsubscribeFromTopic(topic: String) async throws {
        try await Messaging.messaging().unsubscribe(fromTopic: topic)
    }

    func getToken() async throws -> String {
        // The async version of token() can be awaited and will throw on failure.
        return try await Messaging.messaging().token()
    }
}
```

## Implementació control de versions

### Android

Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les
variables ja definides i mostrarà l'alerta segons els valors rebuts:

```kotlin
osamCommons.versionControl(
    language = Language.CA,
    isDarkMode = false, // Opcional, per defecte false
    applyComModStyles = true // Opcional, per defecte true
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
l'aplicació. El que rebem en el callback és l'objecte `VersionControlResponse`. Aquest objecte pot
arribar amb quatre valors possibles:

- **ACCEPTED**: si l'usuari ha escollit el botó d'acceptar/ok
- **DISMISSED**: si l'usuari ha tret el popup
- **CANCELLED**: si l'usuari ha escollit el botó de cancel·lar
- **ERROR**: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el
  popup

### iOS

Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les
variables ja definides i mostrarà l'alerta segons els valors rebuts:

```swift
osamCommons.versionControl(
  language: Language.es,
  isDarkMode: false, // Opcional, per defecte false
  applyComModStyles: true, // Opcional, per defecte true
  f: { versionControlResponse in }
)
```

A la inicialització se li ha de passar el UIViewController de la pantalla que crida al mòdul. Per
cridar al control de versions només cal executar la funció `versionControl()`, facilitar-li l'idioma
en què es vol mostrar el popup i la funció que volem que executi el callback que retorna el mòdul.
El que rebem en el callback és l'objecte `VersionControlResponse` (ACCEPTED, DISMISSED, CANCELLED o ERROR).

## Implementació control de valoracions

### Android

```kotlin
osamCommons.rating(
  language = Language.CA,
  isDarkMode = false, // Opcional, per defecte false
  applyComModStyles = true // Opcional, per defecte true
) {
  // Do something...
}
```

El que rebem en el callback és l'objecte `RatingControlResponse`:
- **ACCEPTED**: s'ha sol·licitat el popup natiu de valoració (Google In-App Review)
- **DISMISSED**: el popup no s'ha mostrat (per exemple, per no complir condicions)
- **ERROR**: error en el procés

### iOS

```swift
osamCommons.rating(
  language: Language.es,
  isDarkMode: false, // Opcional, per defecte false
  applyComModStyles: true, // Opcional, per defecte true
  f: { ratingControlResponse in }
)
```

El que rebem en el callback és l'objecte `RatingControlResponse` (ACCEPTED, DISMISSED o ERROR).

## Implementació per obtenir informació de la plataforma i de l'app

### Informació del dispositiu

Extreu el sistema operatiu, versió i model.

- **Android**: `osamCommons.deviceInformation { response, info -> ... }`
- **iOS**: `osamCommons.deviceInformation(f: { response, info in ... })`

### Informació de l'aplicació

Extreu el nom, versió (nom i codi) de l'aplicació.

- **Android**: `osamCommons.appInformation { response, info -> ... }`
- **iOS**: `osamCommons.appInformation(f: { response, info in ... })`

## Esdeveniments de l'aplicació

### Canvi d'idioma (`changeLanguageEvent`)

Gestiona la lògica de canvi d'idioma: actualitza preferències, envia analítica i actualitza subscripcions a topics de FCM.

- **Android**:
```kotlin
osamCommons.changeLanguageEvent(Language.CA) { response -> }
```
- **iOS**:
```swift
osamCommons.changeLanguageEvent(language: Language.es, f: { response in })
```

**Respostes (`AppLanguageResponse`):**
- **SUCCESS**: Operació completada correctament.
- **UNCHANGED**: L'idioma és el mateix que l'actual.
- **ERROR**: Error en el procés.

### Inici o actualització de l'app (`firstTimeOrUpdateEvent`)

Gestiona la subscripció inicial o actualització del topic de notificacions. S'ha de cridar a l'inici de l'app.

- **Android**: `osamCommons.firstTimeOrUpdateEvent(Language.CA) { response -> }`
- **iOS**: `osamCommons.firstTimeOrUpdateEvent(language: Language.es, f: { response in })`

**Respostes (`AppLanguageResponse`):** `SUCCESS`, `UNCHANGED` o `ERROR`.

## Gestió de Topics i FCM

### Subscripció a topic personalitzat

- **Android**: `osamCommons.subscribeToCustomTopic("TOPIC") { response -> }`
- **iOS**: `osamCommons.subscribeToCustomTopic(topic: "TOPIC", f: { response in })`

**Respostes (`SubscriptionResponse`):** `ACCEPTED` o `ERROR`.

### Desubscripció de topic personalitzat

- **Android**: `osamCommons.unsubscribeToCustomTopic("TOPIC") { response -> }`
- **iOS**: `osamCommons.unsubscribeToCustomTopic(topic: "TOPIC", f: { response in })`

**Respostes (`SubscriptionResponse`):** `ACCEPTED` o `ERROR`.

### Obtenir el token de Firebase (FCM)

- **Android**:
```kotlin
osamCommons.getFCMToken { response ->
    when (response) {
        is TokenResponse.Success -> println(response.token)
        is TokenResponse.Error -> println(response.error)
    }
}
```
- **iOS**:
```swift
osamCommons.getFCMToken { response in
    if let success = response as? TokenResponse.Success {
        print(success.token)
    } else if let error = response as? TokenResponse.Error {
        print(error.error)
    }
}
```

## Comprovar si el dispositiu està en línia

- **Android**: `osamCommons.isOnline { online -> }`
- **iOS**: `osamCommons.isOnline(f: { online in })`

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
    "platform": "IOS",
    "comparisonMode": "LAZY",
    "startDate": 1645311600000,
    "endDate": 1645311600000,
    "serverDate": 1645788600000,
    "title": { "es": "Títol", "en": "Title", "ca": "Títol" },
    "message": { "es": "Missatge", "en": "Message", "ca": "Missatge" },
    "ok": { "es": "OK", "en": "OK", "ca": "OK" },
    "cancel": { "es": "Cancelar", "en": "Cancel", "ca": "Cancel·lar" },
    "url": "https://...",
    "checkBoxDontShowAgain": true,
    "dialogDisplayDuration": 3600,
    "osVersionComparisonMode": 0,
    "osVersion": "13.0",
    "modelComparisonMode": 1,
    "models": ["iPhone14,2"]
  }
}
```

#### Paràmetres destacats:
- **comparisonMode**: `FORCE` (obligatori), `LAZY` (voluntari), `INFO` (informatiu), `NONE` (no mostrar).
- **osVersionComparisonMode**: -1 (tots), 0 (<=), 1 (==), 2 (>=).
- **modelComparisonMode**: 0 (tots), 1 (incloure llista), 2 (excloure llista).
- **checkBoxDontShowAgain**: Si es mostra la casella de "No tornar a mostrar".
- **dialogDisplayDuration**: Temps en segons perquè torni a aparèixer el diàleg si s'ha tancat.

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
    "numAperture": 5
  }
}
```
- **minutes**: Temps mínim entre valoracions.
- **numAperture**: Nombre mínim d'apertures per mostrar el popup.

---
**Idioma per "default"**: S'ha d'utilitzar `Language.parse(value)` en comptes de `valueOf(value)` per evitar excepcions si l'idioma no es troba.
