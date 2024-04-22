# ButtplugIO4J

ButtplugIO4J is a java client library for buttplug.io. See [buttplug.io main page](https://buttplug.io/)
for more information.

## Use

Use this library by referencing this maven project into yours.

```xml
<dependency>
	<groupId>not yet</groupId>
	<artifactId>available :/</artifactId>
	<version>1.0.0</version>
</dependency>
```

To help you, we've created various examples classes :

* [BasicButtplugIoClientExemple](src/main/java/me/margotfrison/buttplugio4j/examples/BasicButtplugIoClientExemple.java)
the most basic client (BasicButtplugIoClient) for this library. Not recommended for any use.
* [AsyncButtplugIoClientExempleWithPromises](src/main/java/me/margotfrison/buttplugio4j/examples/AsyncButtplugIoClientExempleWithPromises.java)
the ButtplugIoClient client used asynchronously that show how to use promises as callbacks.
* [AsyncButtplugIoClientExempleWithSubscriptions](src/main/java/me/margotfrison/buttplugio4j/examples/AsyncButtplugIoClientExempleWithSubscriptions.java)
the ButtplugIoClient client used asynchronously that show how to handle subscription messages such as StartScan.
* [SyncButtplugIoClientExemple](src/main/java/me/margotfrison/buttplugio4j/examples/SyncButtplugIoClientExemple.java)
the ButtplugIoClient client used synchronously that show how to send simple message synchronously. **(perfect for beginners)**

## Overview

This library contains different clients to suit as much users as possible :

* [BasicButtplugIoClient](src/main/java/me/margotfrison/buttplugio4j/client/basic/BasicButtplugIoClientExemple.java)
the most basic client you could think of for the buttplug.io protocol. Not recommended for any use, but it is
here just in case you would want to be more technical or optimized.
* [ButtplugIoClient](src/main/java/me/margotfrison/buttplugio4j/client/basic/ButtplugIoClient.java)
a client with both asynchronous and synchronous capabilities. It also handle the handshake has one method.
**It is recommended for most users**

We also planned to develop a simpler client that would abstract the protocol and suppress the need of
*most* knowledge of this protocol. This is scheduled for the version 1.1.0.

## Build

Use this command to build the project from source and generate a jar file in the
target directory to use it in your projects.

```
mvn clean package
```
