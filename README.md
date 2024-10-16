# DAW project - Gomoku game

## Introduction
This is a project for the 'Desenvolvimento de Aplicações Web' (Web Application Development) discipline of the 
'Licenciatura em Engenharia de Informática e Computadores' (Degree in Informatics and Computer Engineering) from
'Instituto Superior de Engenharia de Lisboa'.
It consists in the development of an web application (back and front ends) for the [Gomuku game](https://en.wikipedia.org/wiki/Gomoku)
For that we'll use (amoung  others):
- Java 17 or 20
- Kotlin
- Docker (for the DB)
- nodeJS
- Spring Framework (Spring Boot, Spring MVC)
- React
- Postman (for documentation and some testing)
- Bootstrap CSS and JS

### To run the application
```terminal
$ ./gradlew dbTestsUp && ./gradlew dbTestsWait && ./gradlew assemble && ./gradlew extractUberjar && ./gradlew composeUp
```

### Authors
- 45824 Nuno Venâncio
- 45828 João Mafra
- 45831 Gonçalo Machuqueiro

[commits mailmap](https://github.com/isel-leic-daw/2023-daw-leic51n-2023-daw-leic51n-g01/tree/main/docs/.mailmap)

### API Documentation
The API documentation can be found [here](https://documenter.getpostman.com/view/24046057/2s9YRGyUc9)

### Backend Instructions
- [Backend documentation](./code/jvm/README.md)

### Frontend Documentation
- [Fontend documentation](./code/js/README.md)
