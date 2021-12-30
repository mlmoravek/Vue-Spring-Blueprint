# frontend

## Project setup
```
npm install
```

### Compiles and hot-reloads for development
```
npm run serve
```

### Compiles and minifies for production
```
npm run build
```

### Lints and fixes files
```
npm run lint
```

### Customize configuration
See [Configuration Reference](https://cli.vuejs.org/config/).


# Visual Studio Code Extensions
Some VSCode Extensions are usefull for this frontend project setup:
* Vetur
* ESLint

Through the project config it is recommended to *NOT* use a prettier or beautifier extension.


# Libraries 

## Vue Environment Variables
https://cli.vuejs.org/guide/mode-and-env.html

Use vue environment variables to define dev and prod specific project variables.

## Frameworks
### Buefy Componenten Framework Variables
https://buefy.org/documentation/customization

The buefy framework is included in the @/plugins/buefy.ts.  
All buefy components can be used in the whole project.  
To override buefy variables use the file @/assets/styles/variables.scss  

To remove uninstall:
* buefy

Also remove the file @/plugins/buefy.ts and bulma from the css files in @/assets/styles/*.

### Bulma CSS Framework
https://bulma.io/documentation/

The bulma library comes with buefy. Remove buefy to remove bulma.

## Icons
### Material Design Icons
https://materialdesignicons.com/

This icon library is the standart icon libray from buefy and installed in this project. To remove uninstall:
* @mdi/font

Also remove the import statement in the @/plugin/buefy.ts file.

### Fontawsome 
https://github.com/FortAwesome/vue-fontawesome  

To use fontawesome icons, install: 
* @fortawesome/fontawesome-svg-core  
* @fortawesome/vue-fontawesome
* @fortawesome/free-solid-svg-icons

Also disomment the @/plugin/fontawesome.ts file and  
discomment the comments in in the main.ts file.

To minimise the build product not all fontawesome icons are includes by default. Define the icons you need and inclide them specificaly in the file @/plugin/fontawesome.ts.

## Axios
https://github.com/axios/axios

Axios is a Promise based HTTP client. The service @/services/RequestService.ts could be used as a wrapper for standart axios usages.


## Vue Router
https://router.vuejs.org/

Vue Router is the standart library for routing in vue. All routes are defined in @/routes/*.

## Vuex
https://vuex.vuejs.org/

Vuex is a state management pattern + library. It serves as a centralized store for all the components in an application, with rules ensuring that the state can only be mutated in a predictable fashion.  
All store files are located in @/store/*

Be aware that only App-State-Context functionality is implemented in a store. Not every data has to be managed by a store if they are not used at differend isolated locations in the application!


# Linting 
https://wim.init.de/display/TC/Frontend+Linter+und+Richtlinien

For script linting ESLint https://eslint.org/ and Prettier https://prettier.io/ are used. 
"vue/essential" and "eslint:recommended" are imported as default rules. Furthermore, some more rules are used for this blueprint. See .eslintrc.js

For css linting StyleLint https://stylelint.io/ is used.


# Best practices Vue development

## CSS Styles
Only use scoped style in components. Put you globale style at @/asserts/styles/*.

## Components 
Components under @/components/* should be developed as encapseled indepemented components which only work with their environment through *props* and emmited *events*. Try to reduce dependencies and make component reusable by less use of app state context functionality. Furthermore, try to capsel funcktionality into components and prevent of building to huge components. 
