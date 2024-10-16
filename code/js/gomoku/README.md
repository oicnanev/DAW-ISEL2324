# Create React App

## Run Create React App

```bash
npx create-react-app gomoku
```

This will install the necessary packages to run React and create a new project called gomoku.
But it doens't use Typecript, so additional packages are needed.

```bash
cd gomoku
npm install --save-dev typescript ts-loader webpack webpack-cli webpack-dev-server @types/react @types/react-dom

# For linting
npm install --save-dev eslint eslint-plugin-react eslint-plugin-react-hooks eslint-webpack-plugin prettier @typescript-eslint/eslint-plugin
```

## To use Typescript in this project need to install the following packages:

```bash
npm install --save-dev typescript @types/node @types/react @types/react-dom
```

## To use serve
```bash
npm install -g serve (linux needs sudo because ```-g``` is a global installation)
```

## To create and run a production build
```bash
npm run build
serve -s build
```
