# modul_comu_osam
[![](https://jitpack.io/v/AjuntamentdeBarcelona/modul_comu_osam.svg)](https://jitpack.io/#AjuntamentdeBarcelona/modul_comu_osam)
[![License: GPL v3](https://img.shields.io/badge/License-GPLv3-blue.svg)](https://www.gnu.org/licenses/gpl-3.0)
[![License](https://img.shields.io/badge/License-BSD%203--Clause-blue.svg)](https://opensource.org/licenses/BSD-3-Clause)
# README
## Com es fa servir?
### Android
- Afegeix aquesta dependència en el teu projecte:
```
implementation 'com.github.AjuntamentdeBarcelona:modul_comu_osam:1.0.10'
```
- Afegir aquest codi al teu build.gradle
```
allprojects {
    repositories {
        maven { url "https://jitpack.io" }
    }
}
```

### iOS
- Per utilitzar el mòdul de control de versions, cal afegir l'arxiu Podfile la ubicació del repositori:
```
pod 'OSAMCommon', :git => 'https://github.com/AjuntamentdeBarcelona/modul_comu_osam.git', :tag => '1.0.10'
```

- Actualitzar mitjançant el comandament 'pod update' les dependències.

## Introducció

Aquest mòdul uneix el control de versions i el control de valoracions. 

En el control de versions es mostrarà un avís quan el servei avisi que hi ha una nova versió de l'app.
Aquesta alerta la podem mostrar amb un missatge amb botons de confirmació d'accions.

Tindrem tres diferents tipus d'alerta:

1. Informativa: Alerta amb un missatge i / o un títol informatiu, amb un botó d ' "ok" per confirmar que s'ha llegit.
2. Restrictiva: Alerta amb un missatge i / o un títol, amb botó d ' "ok" que un cop fet clic redirigirà l'usuari a una url.
3. Permisiva: Alerta amb un missatge i / o un títol, amb botons de "ok" i "cancel". Si fem clic al botó de cancel·lar l'alerta desapareixerà, i si ho fem al de confirmar s'obrirà una url.

Pel que respecta al control de valoracions, la seva funcionalitat és mostrar periòdicament una popup que convida a l’usuari a deixar un comentari sobre l'app al market place corresponent (Google Play o AppStore).
Per a Android, el popup té tres botons:

- Positiu (“VALORAR ARA”): El sistema obre la web de l’app client en el market, i l’usuari només haurà de fer ‘new review’ i deixar el seu comentari i valoració sobre l’app.
- Negatiu (“NO, GRÀCIES”).  El popup es tanca i no tornarà a aparèixer.
- Neutre (“MÉS TARD”). El popup es tanca i tornarà a aparèixer en un futur.

En canvi, per a iOS s'utilitza la llibreria nativa:
```
SKStoreReviewController.requestReview()
```

## Descàrrega del mòdul
Des de la OSAM es proporcionen mòduls per realitzar un conjunt de tasques comunes a totes les apps publicades per l'Ajuntament de Barcelona.

El mòdul comú (IOS / Android) està disponible com a repositori a:
https://github.com/AjuntamentdeBarcelona/modul_comu_osam

## Configuració del mòdul

Per tal de poder utilitzar el mòdul, és necessari especificar en temps de inicialització els següents paràmetres:
- backendEndpoint: url del backend del mòdul comú
- crashlyticsWrapper: implementació del wrapper de crashlytics que hem de implementar (o de qualsevol altre llibreria)
- analyticsWrapper: implementació del wrapper de analytics que hem de implementar (o de qualsevol altre llibreria)

A continuació, es detalla per cada plataforma, com es realitza aquesta inicialització.
Per a més detalls de com integrar el mòdul comú amb la CI de la OSAM, consultar el manual de la CI.

### Android

Inicialitzarem el mòdul comú de la següent manera:

```
private val osamCommons by lazy {
         OSAMCommons(
            this,
            backendEndpoint = getString(R.string.common_module_endpoint),
            crashlyticsWrapper = CrashlyticsWrapperAndroid(),
            analyticsWrapper = AnalyticsWrapperAndroid()
         )
    }
```
A continuació s'indiquen les implementacions del wrapper de crashlytics i analytics:
```
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
```

### iOS

Inicialitzarem el mòdul comú de la següent manera:

```
lazy var osamCommons = OSAMCommons(
    vc: self, backendEndpoint: <url_endpoint_modul_comu>,
    crashlyticsWrapper: CrashlyticsWrapperIOS(),
    analyticsWrapper: AnalyticsWrapperIOS()
  )
```
A continuació s'indiquen les implementacions del wrapper de Crashlytics i Analytics:
```
class CrashlyticsWrapperIOS: CrashlyticsWrapper {
    func recordException(className: String, stackTrace: String) {
        let exception = ExceptionModel(name: className, reason: stackTrace)
        Crashlytics.crashlytics().record(exceptionModel: exception)
    }
}

class AnalyticsWrapperIOS: AnalyticsWrapper {
    func logEvent(name: String, parameters: [String : String]) {
        Analytics.logEvent(name, parameters: parameters)
    }
}
```

## Implementació control de versions
### Android
Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les variables ja definides i mostrarà l'alerta segons els valors rebuts:

```
osamCommons.versionControl(
  language = Language.CA
  ) {
    // Do something...
  }
```
A la inicialització se li ha de passar el context de l'app.
Per cridar al control de versions només cal executar la funció "versionControl" i facilitar-li l'idioma en què es vol mostrar el popup. Per facilitar l'idioma, la llibreria inclou la classe "Language" que conté Català (CA), Castellà (ES) i Anglès (EN), que són els idiomes suportats.
Com a extra, se li ha afegit un callback perquè la pantalla principal pugui reaccionar en cas que hi hagi hagut un error o si, a part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de l'aplicació.
Lo que revem en el callback es l'objecte "VersionControlResponse". Aquest objecte pot arrivar amb quatre valors possibles:

- ACCEPTED: si l'usuari ha escollit el botó d'acceptar/ok
- DISMISSED: si l'usuari ha tret el popup
- CANCELLED: si l'usuari ha escollit el botó de cancel·lar
- ERROR: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el popup

Per exemple: Si l'usuari cancel·la el popup, al callback rebriem l'objecte "VersionControlResponse.CANCELLED".  Si en el cas de que volgués fer alguna acció diferent si l'usuari cancel·la el popup, es podria definir en aquest punt la casuistica.

### iOS
Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les variables ja definides i mostrarà l'alerta segons els valors rebuts:

```
osamCommons.versionControl(
  language: Language.es,
  f: {_ in }
)
```
A la inicialització se li ha de passar el UIViewController de la pantalla que crida al mòdul.
Per cridar al control de versions només cal executar la funció "versionControl", facilitar-li l'idioma en què es vol mostrar el popup i la funció que volem que executi el callback que retorna el mòdul.
Per facilitar l'idioma, la llibreria inclou la classe "Language" que conté Català (CA), Castellà (ES) i Anglès (EN), que són els idiomes suportats.
Pel que respecta al callback, s'ha afegit perquè la pantalla pugui reaccionar en cas que hi hagi hagut un error o si, a part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de l'aplicació.
Lo que revem en el callback es l'objecte "VersionControlResponse". Aquest objecte pot arrivar amb quatre valors possibles:

- ACCEPTED: si l'usuari ha escollit el botó d'acceptar/ok
- DISMISSED: si l'usuari ha tret el popup
- CANCELLED: si l'usuari ha escollit el botó de cancel·lar
- ERROR: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el popup

Per exemple: Si l'usuari cancel·la el popup, al callback rebriem l'objecte "VersionControlResponse.CANCELLED".  Si en el cas de que volgués fer alguna acció diferent si l'usuari cancel·la el popup, es podria definir en aquest punt la casuistica.

## Implementació control de valoracions
### Android
Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les variables ja definides i mostrarà l'alerta segons els valors rebuts:

```
osamCommons.rating(
  language = Language.CA
  ) {
    // Do something...
  }
```
A la inicialització se li ha de passar el context de l'app.
Per cridar al control de valoracions només cal executar la funció "rating" i facilitar-li l'idioma en què es vol mostrar el popup. Per facilitar l'idioma, la llibreria inclou la classe "Language" que conté Català (CA), Castellà (ES) i Anglès (EN), que són els idiomes suportats.
Com a extra, se li ha afegit un callback perquè la pantalla principal pugui reaccionar en cas que hi hagi hagut un error o si, a part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de l'aplicació.
Lo que revem en el callback es l'objecte "RatingControlResponse". Aquest objecte pot arrivar amb quatre valors possibles:

- ACCEPTED: si l'usuari ha escollit el botó d'acceptar/ok
- DISMISSED: si l'usuari ha tret el popup o no compleix les condicions per mostrar-li a l'usuari
- CANCELLED: si l'usuari ha escollit el botó de cancel·lar
- LATER: si l'usuari ha escollit que se li recordi "més tard"
- ERROR: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el popup

Per exemple: Si l'usuari cancel·la el popup, al callback rebriem l'objecte "RatingControlResponse.CANCELLED".  Si en el cas de que volgués fer alguna acció diferent si l'usuari cancel·la el popup, es podria definir en aquest punt la casuistica..

### iOS

Per crear el missatge d'alerta, únicament hem de cridar a la funció que descarregarà el json amb les variables ja definides i mostrarà l'alerta segons els valors rebuts:

```
osamCommons.rating(
  language: Language.es,
  f: {_ in }
)
```
A la inicialització se li ha de passar el UIViewController de la pantalla que crida al mòdul.
Per cridar al control de valoracions només cal executar la funció "rating", facilitar-li l'idioma en què es vol mostrar el popup i la funció que volem que executi el callback que retorna el mòdul.
Per facilitar l'idioma, la llibreria inclou la classe "Language" que conté Català (CA), Castellà (ES) i Anglès (EN), que són els idiomes suportats.
Pel que respecta al callback, s'ha afegit perquè la pantalla pugui reaccionar en cas que hi hagi hagut un error o si, a part de la funcionalitat que ofereix la llibreria, es vol afegir alguna funcionalitat més pròpia de l'aplicació.
Lo que revem en el callback es l'objecte "RatingControlResponse". Aquest objecte pot arrivar amb tres valors possibles:

- ACCEPTED: s'ha sol·licitat que surti el popup natiu de valoració d'iOS: SKStoreReviewController
- DISMISSED: el popup no compleix les condicions per mostrar-li a l'usuari
- ERROR: si hi ha hagut cap error al procés d'obtenir la informació necessaria o al mostrar el popup

Per exemple: Si l'usuari treu el popup, al callback rebriem l'objecte "RatingControlResponse.DISMISSED".  Si en el cas de que volgués fer alguna acció diferent si l'usuari ha tret el popup o aquest no compleix les condicions per sortir, es podria definir en aquest punt la casuistica..


## Format JSONs
### Control de Versions
```
"data": {
        "id": 109,
        "appId": 400,
        "packageName": "cat.bcn.commonmodule",
        "versionCode": 2021050000,
        "versionName": "1.0.0",
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
```
#### Paràmetres
- packageName
    - Obligatori
    - Especifica el ApplicationID o BundleID de l'app que afecta
- versionCode
  - Obligatori
  - Especifica la versió a la que afecta el control de versions
- platform
  - Obligatori
  - Especifica per a quina plataforma (ANDROID o IOS) afecta
- comparisonMode
    - Obligatori
    - Especifica la manera de comparació de la versió de l'app amb el mòdul
- title
    - Obligatori
    - Títol de l'alerta en el cas que s'hagi de mostrar.
- message
    - Obligatori
    - Missatge de l'alerta en cas que s'hagi de mostrar.
- ok
    - Opcional
    - Títol del botó d'acceptar.
    - Si es rep aquest paràmetre juntament amb el paràmetre okButtonActionURL, es mostrarà en l'alerta un botó d'acceptar que obrirà el link que s'ha especificat en el paràmetre okButtonActionURL.
- cancel
    - Opcional
    - Títol del botó de cancel·lar
- url
    - Opcional
    - Link que s'obrirà quan l'usuari seleccioni el botó d'acceptar. Per exemple: link de la nova versió de l'aplicació a l'App Store / Google Play.

### Control de Valoracions
```
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
```
#### Paràmetres
- appStoreIdentifier
  - Obligatori
  - Especifica el id de l'app al AppStore per poder valorar-la
- packageName
    - Obligatori
    - Especifica el ApplicationID o BundleID de l'app que afecta
- platform
  - Obligatori
  - Especifica per a quina plataforma (ANDROID o IOS) afecta
- minutes
    - Obligatori
    - Especifica el temps (en minuts) que ha de passar perquè surti el popup
- numAperture
    - Obligatori
    - Especifica la quantitat de vegades que s'ha d'obrir l'app perquè surti el popup
- message
    - Obligatori
    - Missatge de l'alerta en cas que s'hagi de mostrar.
  
  
## Com funciona el mòdul de control de versions
Depenent del valor del paràmetre "comparisonMode" mostrarem l'alerta.

Aquest paràmetre compararà la versió instal·lada amb la qual rebem del json, en funció de tres valors:

  -> FORCE: Mostra l'alerta i no es pot treure. Actualització obligatoria
  -> LAZY: Mostra l'alerta amb l'opció d'actualitzar l'app o seguir utilitzant l'actual. Actualització voluntaria
  -> INFO: Mostra l'alerta amb un missatge informatiu. Deixa seguir utilitzant l'app amb normalitat
  -> NONE: no es mostra el popup
  
## Com funciona el control de valoracions
- L’app compta cada vegada que s’obre (s'ha de cridar el mètode "rating" de la llibreria)
- L’app espera a que passin un nº de minuts determinats (p.ex. 90) des de l’últim cop que ha mostrat la pop up (per tal de l’usuari no la consideri intrusiva o abusiva).
- Un cop passats aquests dies i quan el comptador superi un valor determinat (p.ex. 20), mostra el popup i el comptador es reinicia independentment de la resposta de l’usuari.*
- La operativa no es veu modificada si hi ha un canvi de versió (és a dir, es mantenen els valors de comptatge de dies i de nº de apertures).
- El text que es mostra al popup és configurable des del servei

*En iOS es crida al SKStoreReviewController i la mateixa llibreria s'encarrega de quan mostrar-se
