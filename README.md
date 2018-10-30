# opencms-module
Opencms module gradle plugin

##1. Installation

First you need to clone this Git repository with:

`
  git clone https://github.com/Thomas-Adams/opencms-module.git
`

In order to work with gradle and specifically this repository you need to have Maven installed with a valid
configuration of a local repository (see here for details: https://maven.apache.org/guides/getting-started/index.html).

Change into the projects directory and call `gradlew install` on the command line in order to install the plugin into your local repository.

##2. Structure of a OpenCms module:

    src
     |
     ---- main
            |
            ---- java
            |
            ---- resources
            |
            ---- vfs
                   |
                   ---- system
                   |         |
                   |         ---- modules
                   |                |
                   |                ${opencms.module.name}
                   |                     |
                   |                     ---- classes                                 
                   |                     |
                   |                     ---- elements
                   |                     |
                   |                     ---- formatters
                   |                     |
                   |                     ---- lib
                   |                     |
                   |                     ---- resources
                   |                     |
                   |                     ---- schemas
                   |                     |
                   |                     ---- templates  
                   |         
                   |
                   ---- dependencies.xml
                   |
                   ---- explorertypes.xml
                   |
                   ---- exportpoints.xml
                   |
                   ---- module.properties
                   |
                   ---- parameters.xml
                   |
                   ---- relations.xml
                   |
                   ---- resources.xml
                   |
                   ---- resourcetypes.xml
    test
     |
     ---- java
     |
     ---- resources
     
      
