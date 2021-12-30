
# Spring RESTful backend application
Als Backend-Framework für user Blueprint verwenden wir das Spring Boot Framework. 
Diese Blueprint implemeniert einige Anwendungs-Szenarien, welche in kommenden Projekten sehr hilfreich sein kann.  
Z.B. ist bereits die Absicherung von Endpunkten mittels User-Authentication implementiert. Außerdem demonstriert das Blueprint eine Reihe von nützlichen Funktionalitäten, wie eine CRUD-, Such-, oder Paging- Funktionalität für eine Enität. 

Im Backend sind zum besseren Verständnis der Funktionalität mancher Klassen einzelne Links mit angegeben.

# Datenbank
Im Blueprint wird die h2 In-Memory Datenbank verwendet. Bedeutet bei jedem starten des Blueprints wird die Datenbank neu aufgesetzt und mit den default Daten initialisiert.  
Die Datebank kann einfach in der *application.properties* geändert werden. Die Konfiguration 
einer Oracle Datenbank ist hier bereits hinterlegt und muss nur entkommentiert werden.

Auf die Datenbank kann über http://localhost:8080/api/h2-console/ zugegriffen werden.  
username=sa  password=password

# application.properties
In der *application.properties* Datei können einige Anwendungs spezifischen Variablen gesetzt werden. 

* app.jwt.secret -  der JWT secret key 
* app.jwt.expiration_time - die JWT ablaufs Zeit
* app.endpoint.api - der allgemeine Pfad für die REST-Schnittstellen 
* app.endpoint.registration - der Pfad um einen neuen Benutzer anzulegen
* app.endpoint.login - der Pfad um sich einzulogen 
* app.endpoint.frontend - der Pfad über den das Frontend ausgeliefert wird
* app.frontend.path - der Pfad wo das Frontend liegt

# Controller
In den Controllern sind die eigentlochen Endpunkte definiert. Allen Endpunkten ist die in *application.properties* angegebene Api URL vorangestellt.

## Exception Handling
Die Controller benötigen im allgemeinen kein direktes Exception Handling da über die Klasse *GlobalExceptionHandler* ein allgemeines Exception Handling ausgeführt wird. Hier kann für einzelne Exceptions festgelegt werden, was geschieht, bzw wie die Response aussehen soll. 

## FrontendController
Mit dem FrontendContoller kann wie der Name bereits sagt das Frontend über das Backend ausgeliefert werden.    

Der Pfad über den das Frontend abzurufen ist und der Pfad wo das Frontend auf dem Server liegt ist über die *application.properties* anzugeben.

In der FrontendController Klasse selber, muss angegeben werden, welche einzelnen Webseiten das Frontend besitzt. Eine normale Vue Single-Page-Application besitzt nur eine HTML Seite: *index.thml*. Die Seite ist dabei ohne *.html* anzugeben.

```
private final Set<String> pages = Set.of("index");
```
## AuthController
Der AuthController ist eine Erweitung der Authentication Komponente. In ihm ist der Entpunkt zur registrierung neuer Benutzer hinterlegt. Der Endpunkt zum einloggen wird über Spring-Security bereits implementiert.  
In der *application.properties* ist hierbei nur der Login-Pfad als auch der Registrierungs-Pfad anzugeben.

## UserController
Im UserController sind Endpunkte für den eingeloggten Benutzer implemenitert. Über diese kann er seine Attribute/Passwort ändern.


# Komponenten
In diesem Blueprint sind bereits einige Komponenten für einen schnellen entwicklungsstart im Projekt for implementiert. 

## Authentication
Die Komponente Authentication wird eine zustandsloses Login verfahren implementiert. Die zustandslosigkeit wird mittels JWT-Tokens erreicht. Beim Login eines Benutzers wird ein JWT-Token zurück gegeben. Dieser wird für den aufruf von gesicherten Endpunkten benötigt.  
Bei *development environment* wird automatisch ein Admin Benutzer (username: *admin*, password: *admin*) angelegt.
Die Login versuche eines Benutzers werden über einen *LoadingCache* gespeichert. Schlägt der Login bei einem Benutzer über 10 mal fehlt, wird der Benutzer gespert (*User.enabled = false*).  

### User  
Benutzer werden in der Datebank mit der *de.init.backend.authentication.model.User* Entität gespeichert.  
Der *de.init.backend.authentication.AuthenticationUserService* stellt eine reihe von Funktionen bereit, mit dennen neue Benutzer angelegt, der aktuelle User abgefragt oder Attribute des aktuellen Nutzer geändert werden können.


Check auth by user roles for specific route with the @PreAuthorize annotation provided by Spring Security. This annotation can be applied to a class or method, and it accepts a single string value.

Roles and authorities are similar in Spring. The main difference is that, roles have special semantics – starting with Spring Security 4, the ‘ROLE_‘ prefix is automatically added (if it's not already there) by any role related method.

So hasAuthority(‘ROLE_ADMIN') is similar to hasRole(‘ADMIN') because the ‘ROLE_‘ prefix gets added automatically.
In this application wie use 'hasAuthority', because we have a double layer role system. 
* https://www.baeldung.com/spring-security-check-user-role
* https://www.baeldung.com/spring-security-expressions

### Roles & Permissions
Es ist ein hirariches Rollen System implementiert.  
Zu einer Rolle können mehrere Permissions hinterlegt werden. Unterschiedliche Rollen können auch die gleichen Permissions haben. Einem Benutzer können dann eine bis mehrere Rollen zugewiesen werden. Dem Benutzer werden dann die Permissions aller seiner Rollen verliehen. 

Über eine Implementierung der Klasse *de.init.backend.authentication.RoleConfiguration* (im Blueprint in *de.init.backend.RoleConfig*) können die einzelnen Permissions und Rollen angegeben werden.
Inder zu implementierenden *getRole()* Methode muss eine mapping der Rolen-Permissions zurück gegeben werden (`Map<RoleName, List<PermissionName>>`).

### Sichere Endpunkte Konfiguration
Um die Authentication Komponente verwenden zu können, muss die Klasse *de.init.backend.authentication.AuthSecurityConfiguration* implementiert werden (im Blueprint in *de.init.backend.SecurityConfig*).  
In dieser muss die Methode *configure(HttpSecurity http)* überschrieben werden. Hier können dann die einzelnen Endpunkte der Anwendung als authorisierten oder öffentlichen Endpunkt konfiguriert werden. Stelle sicher das jedoch auch die *super()* Methode der *configure* Funktion aufgerufen wird.  
Hier kann nun für ganze Pfade oder einzelne Enpunkte konfiguriert werden, ob ein Benutzer eingelogt sein muss und welche Rolle er haben muss.

### Einzelne Berechtigungen für Endpunkte 
Neben der Konfiguration der Pfade in der *SecurityConfig* Klasse, kann auch direkt in den Controllern an einzelnen Methoden angegebn werden, welche Permission der Benutzer haben muss, um diesen Endpunkt verwenden zu können. Dafür muss der Entpunkt mit folgender Annotation versehen werden: 

```
@PreAuthorize("hasAuthority('PERMISSION_XYZ')")
```


## Datatable
Mit der generischen Datatable Komponente kann in wenigen Minuten eine vollumfängliche Entität mit CRUD Funktionalität bzw. Paging augestatet werden. 

Die Komponente bietet zwei Services, welche zur Laufzeit instanziert werden müssen (also keine Spring übliche dependency injection). 

### CrudService
Der generische *de.init.backend.datatable.CrudService* implementiert alle CRUD-Funktionalitäten.

### Instanziieren
Beim instanziieren dises Services muss einmal ein vom *org.springframework.data.repository.CrudReposirty* abgeleitetes Repository, als auch die jeweilige Klasse der Entität übergeben werden. Desweiteren muss der Service einmal mit der Entitäts-Klasse, als auch dem Type des Id-Properties der Entitäts-Klasse typisiert werden. 

``` 
CrudService<Entität, Long> crudService = new CrudService<Entität, Long>(repository, Entität.class);
```

### Aufruf Update Methode 
Beim Aufruf der *CrudService.update(id, updatedEntity)* Methode, werden nur die Attribute der Entität aus der Datebank mit den Attribute der *updatedEntity* überschrieben, die die Annotation *Editable* haben. 
 
Die Datatable Komponente stellt hierfür die *de.init.backend.datatable.annotation.Editable* Annotation bereit. 
 
Mann kann jedoch der *update()* Methode auch, mittels der übergabe einer Liste von *java.lang.reflect.Field* Objekten angeben, welche Attribute überschrieben werden können. 

### PageService
Der generischen *de.init.backend.datatable.PageService*, implementiert einmal eine Paging Funktionalität, als auch eine Such-Funktionalität.  

### Instanziieren
Beim instanziieren dises Services muss einmal ein vom *orgde.init.backend.datatable.DataTableRepository* abgeleitetes Repository, als auch die jeweilige Klasse der Entität übergeben werden. Das *DataTableRepository* implementiert bereits eine reihe von Funktionlitäten, da es von *PagingAndSortingRepository* abgeleitet ist, was selber von *CrudReposirty* abgeleitet ist.
Desweiteren muss der Service einmal mit der Entitäts-Klasse, als auch dem Type des Id-Properties der Entitäts-Klasse typisiert werden. 

``` 
PageService<Entität, Long> pageService = new PageService<Entität, Long>(repository, Entität.class);
```

### Suche
Neben der Page-Funktionalität ist in diesem Service mit der Methode *PageService.search()* auch eine Such-Funktionalität implemenitert. Der Methode kann ein Suchquery nach der rsql Spezifikation übergeben werden (github.com/jirutka/rsql-parser).  
Über die Annotation *de.init.backend.datatable.annotation.Searchable* kann definiert werden, über welche Attribute der Klasse gesucht werden kann. Dazu müssen die Attribute  mit *Searchable* annotiert werden. 

Mann kann jedoch der auch *search()* Methode eine Liste von *java.lang.reflect.Field* Objekten übergeben, welche die Attribute angibt, über die gesucht werden soll.

### Page  
Die eigentliche Page-Funktionalität des Services wird mit der Methode *getPage()* bereitgestellt. Diese erwartet eine Reihe von Parametern:
* page - Page index
* size - Size of the pages
* sortBy - Sort by column name
* ascending - Sort direction ASC (true) or DESC(false)
* search - A RSQL search string
* filter - A RSQL filter string
* (searchableFields - Set of Field to search in)
