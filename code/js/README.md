# Gomuku - Frontend Documentation

> This is the frontend documentation for the Gomuku project.

## Table of Contents

* [Introduction](#introduction)
* [Code Organization](#code-organization)
* [API Connectivity](#api-connectivity)
* [Authentication and Session Management](#authentication-and-session-management)
* [Conclusions - Critical Evaluation](#conclusions---critical-evaluation)

---

## Introduction

The frontend is a single page application built using React.

This application is a client for the Gomuku API, which is documented [here](https://documenter.getpostman.com/view/24046057/2s9YRGyUc9).
For more information about the backend, please refer to the [backend documentation](../jvm/README.md).

---

## Code Organization

The frontend code is organized in the following way:

* `js`
  * `gomuku`
      * `public` - Contains the `index.html` file, the `robots.txt` and the `manifest.json` file;
        *  `res` - Contains the images for pieces and board, page wallpaper and favicon;  
      * `src`
          * `res` - Contains the images for pieces and board, page wallpaper and favicon;
          * `Components` - Contains the React components and pages used in the application;
          * `App.tsx` - The main component of the application;
          * `index.tsx` - The entry point of the application;

In the `js` folder, there are other files used for the development of the application, like the `package.json` file,
the `tsconfig.json` file, the `webpack.config.js` file, linter configuration files, etc.

---

## API Connectivity

The media types used in the communication with the API are the following:

 * `application/json` - Used in the request bodies;
 * `application/problem+json` - Used in the response bodies when an error occurs;
 * we `have application/vnd.siren` in the api but don't use it in the frontend

---

## Authentication and Session Management

The user authentication is done in the `Login` or `Register` pages.
We use the components `Auth` and `RequireAuthn` to manage the user session.

The user session is stored in the browser's local storage with a coookie named `gomoku` that has the user's `username`.

---

## Conclusions - Critical Evaluation

In conclusion, we can say that the frontend was challenging, but we were able to implement great part of the requirements.

The application is not  fully functional.
Examples of missing features are:

* Allow user to give up game on lobby;
* Allow user to give up game on game page;
* Inform user of victory, defeat or draw;
* Refresh the game page when the opponent makes a move, polling the server;
* Design a proper bootstrap css them for the board (Game in action);

The main challenges of the second phase of the project were:

* Implementing the game logic;
* Managing the navigation state;
* Managing the game state;
