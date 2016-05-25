# Entitas ECS - Scala / Scala.js


Scala implementation port of https://github.com/sschmid/Entitas-CSharp


### Entitas cli
use entitas cli to generate empty components & extensions

    git clone git@github.com:darkoverlordofdata/entitas-ts.git
    cd entitas-ts
    npm install . -g

    Scala Usage:
    entitas init namespace
    entitas create -c name field:type... 
    entitas generate -p scala
    
    Options:
    -c  [--component] # create a component
    -p  [--platform]  # target platform for generated code



### Disambiguation

There are 2 repo's
https://github.com/darkoverlordofdata/entitas-scala-js
https://github.com/darkoverlordofdata/entitas-scala

The 1st, js version uses sbt and thus is the source version for maven
The 2nd uses gradle, and the entitas folder is just a copy