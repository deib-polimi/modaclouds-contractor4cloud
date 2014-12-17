# MODAClouds Contractor4Cloud

## Description

Library to select the optimal contract for cloud resources used in the SPACE4Cloud project.

## Usage

The main class of this project is called `Contractor`. Here is an example of how it is used:

```java
import it.polimi.modaclouds.space4clouds.contractor4cloud.Contractor;
public class Example {
    public static void main(String[] args) {
        String basePath       = "/Users/ft/Development/workspace-s4c-runtime/Constellation/";
        String configuration  = basePath + "OptimizationMac.properties";
        String solution       = basePath + "ContainerExtensions/Computed/Solution-Conference-Amazon.xml";
        
        Contractor.removeTempFiles = false;
        
        int daysConsidered = 400;
        double percentageOfS = 0.5;
        double m = 1000.0;
        
        File f = Contractor.perform(configuration, solution, daysConsidered, percentageOfS, m);
        if (f != null && f.exists())
            System.out.println("Solution: " + f.getAbsolutePath());
        else
            System.out.println("No solution!");
    }
}
```

## Installation

To use this tool you need to add it as a maven dependency:

* Group Id: it.polimi.modaclouds.space4cloud
* Artifact Id: contractor4cloud
* Version: 0.0.2
* Type: jar
* Scope: compile.

You must add then the reference to the downloaded jar to the manifest of the project.
