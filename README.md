Oak Utility App
===============

Web app hosting few utilities for [Apache Jackrabbit Oak][1] using Google Appengine.

In action: [oakutils.appspot.com](http://oakutils.appspot.com)

[1]: http://jackrabbit.apache.org/oak

## Deploy

        jdk 11
        open https://cloud.google.com/sdk/docs/install-sdk
        gcloud auth login
        mvn package appengine:deploy -Dapp.deploy.projectId=oakutils -Dapp.deploy.version=20240703t163000
