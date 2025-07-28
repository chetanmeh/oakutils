Oak Utility App
===============

**Notice: This project is no longer maintained. Use [Oak Tools](https://thomasmueller.github.io/oakTools/) instead.**

Web app hosting few utilities for [Apache Jackrabbit Oak][1] using Google Appengine.

In action: [oakutils.appspot.com](http://oakutils.appspot.com)

[1]: http://jackrabbit.apache.org/oak

## Deploy

    jdk 11
    open https://cloud.google.com/sdk/docs/install-sdk
    gcloud auth login
    mvn package appengine:deploy -Dapp.deploy.projectId=oakutils -Dapp.deploy.version=20240709t113000

## Local Development

    Consider adding sufficient unit tests, and using
    http://gaelyk.appspot.com/tutorial/run-deploy#run