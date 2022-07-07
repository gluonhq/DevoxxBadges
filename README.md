# DevoxxBadges

App to scan badges using the Barcode Scanner service

## Pre-requisites

Please checkout the prerequisites to run this application [here](https://github.com/gluonhq/gluonfx-maven-plugin#requirements).

## Instructions

> **Note**: The following are command line instructions. For IDE specific instructions please checkout [IDE section](https://docs.gluonhq.com/#_ide) of the Gluon documentation.

These application can run on the JVM on desktop platforms. To run the application, execute the following command:

```
mvn gluonfx:run
```

The same application can also run natively for on any targeted OS, including Android, iOS, Linux, Mac and Windows.

To create a native image, execute the following command:

```
mvn gluonfx:build gluonfx:package gluonfx:install gluonfx:nativerun
```
