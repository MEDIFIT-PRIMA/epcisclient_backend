# epcisclient_backend
Utility backend app for generating and capturing EPCIS 2.0 json-ld Object-Events, based on the FSKX Schema https://github.com/MEDIFIT-PRIMA/fsklab-json.
- Backend written in Kotlin with Ktor at src/main/kotlin/com/github/medifitprima/epcisclient/Application.kt
### EPCIS Service Endpoint
- https://epcis.medifit-prima.net/


### Endpoints
| Route | Description| Request Body|
| ----- | ---------- | --------------|
| /registerModelURL | Build and capture ObjectEvent (ADD) as `json-ld` (EPCIS 2.0) from given FSKX model URL. Capture ID in response header. |`{ "url" : <URL to FSKX model file>}` | 
| /captureExecutionEvent | Build and capture ObjectEvent (OBSERVE) as `json-ld` (EPCIS 2.0). Capture ID in response header. |`{ "model_id" : <UUID>,"model_name" : <model name>}` |







## Configuration
The application requires a configuration file named *epcis_backend.properties* with settings needed for execution. This file can be either located at the user folder or at `CATALINA_HOME` (KNIME Server). In case both locations have the file, the file at the user folder takes precedence.

### How to find CATALINA_HOME
The environment variable `CATALINA_HOME` holds the path to the base directory of a Catalina environment. This can be checked when running the KNIME Server at the beginning. For example when running the startup script of the KNIME server or doing catalina run:

```
Using CATALINA_BASE:   /Applications/KNIME Server/apache-tomee-plus-7.0.5
Using CATALINA_HOME:   /Applications/KNIME Server/apache-tomee-plus-7.0.5
Using CATALINA_TMPDIR: /Applications/KNIME Server/apache-tomee-plus-7.0.5/temp
Using JRE_HOME:        /Library/Java/JavaVirtualMachines/jdk1.8.0_271.jdk/Contents/Home
Using CLASSPATH:       /Applications/KNIME Server/apache-tomee-plus-7.0.5/bin/bootstrap.jar:/Applications/KNIME Server/apache-tomee-plus-7.0.5/bin/tomcat-juli.jar
Tomcat started.
```

### Contents of the file

The *epcis_backend.properties* is a simple Java properties file with the following keys:
* `api-key`: API-KEY (request header) for accessing EPCIS event repository
* `api-key-secret`: API-KEY-SECRET (request header) for accessing EPCIS event repository
```

## References
-	IntelliJ IDEA https://www.jetbrains.com/idea/ (Community edition)
-	Kotlin language for backend https://kotlinlang.org/docs/reference/
-	REST library used https://ktor.io/
-	Frontend with Freemarker templates. Guide https://ktor.io/docs/website.html
-	Deployment of Ktor app with Tomcat https://ktor.io/docs/war.html