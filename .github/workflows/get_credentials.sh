#!/bin/bash

## Get the credentials
wget $CREDENTIALS_FILE_LINK -O src/main/resources/credentials.json > /dev/null 2>&1
wget -P tokens $OAUTH_TOKEN_FILE_LINK  > /dev/null 2>&1
wget -P tdlib $TDLIB_SESSION_FILE_LINK  > /dev/null 2>&1
