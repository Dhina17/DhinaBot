# DhinaBot

A simple telegram bot written in java for my personal use. Feel free to use if you want.

## Requirements

- Java 11+

- Maven

## Features

- Mirror bot (direct link, telegram files)

- Dogbin - paste and get content.

## To Do List

- [ ] Add torrent download support

- [ ] Beautify the progress message

- [ ] Add To Do List remainder support

## Configs

- `API_HASH` - Telegram API hash. Get [here](https://my.telegram.org) by login your telegram account.

- `API_ID` - Telegram API ID. Get [here](https://my.telegram.org) by login your telegram account.

- `BOT_TOKEN` - Your telegram bot token. Check [here](https://core.telegram.org/bots#6-botfather)

- `BOT_USERNAME` - Your telegram bot username.

- `CREATOR_ID` - Your telegram user ID. There are some bots to know your user id. One of them - [userinfobot](https://t.me/userinfobot) or search `userinfobot` in telegram.

- `CREDENTIALS_FILE` - Your `credentials.json` file direck link.

- `GDRIVE_INDEX_LINK` - Your gdrive index link without trailing `/`.  Example - `https://mirror.dhina17.workers.dev/0:` . There is a lot of gdrive index out there. Simply do a github search.

- `HEROKU_API_KEY` - Go to your [heroku](https://heroku.com) account settings, You will get it.

- `HEROKU_APP_NAME` - Name of your heroku app.

- `HEROKU_EMAIL` - Your email id associated with your heroku account.

- `OAUTH_TOKEN_FILE` - Your `StoredCredential` file direct link.

- `TDLIB_SESSION_FILE` - Your `td.binlog` file direct link.

- `TEAM_DRIVE_ID` - Your Gdrive shared drive ID if you are using shared drive.

- `USE_TEAM_DRIVE` - true if you are using shared drive else set it to false.

## How to build

- Clone this repo to your local pc.

    `git clone https://github.com/Dhina17/DhinaBot` or Download this repo as zip and unzip

- Go to DhinaBot folder.
    In terminal , type the following commands

    `mvn clean package assembly:single`

    After compiled successfully, you will get the fat jar with name `dhinabot-$version-jar-with-dependencies.jar` in target folder.

## How to get credentials.json

- Enable Google Drive API. Refer [here](https://developers.google.com/drive/api/v3/enable-drive-api)

- Open the [Google API Console Credentials page](https://console.developers.google.com/apis/credentials)

- Click Select a project, then NEW PROJECT, and enter a name for the project, and optionally, edit the provided project ID. Click Create.

- On the Credentials page, select Create credentials, then OAuth client ID.

- You may be prompted to set a product name on the Consent screen; if so, click Configure consent screen, supply the requested information, and click Save to return to the Credentials screen.

- Select Desktop app for the Application type, and enter any additional information required.

- Click Create.

- A dialog will be prompt with your client ID and secrets, Click Ok.

- You can see the download button in the OAuth 2.0 Client IDs section, Click and download it.

- Rename the downloaded file to `credentials.json`

> Reference : [here](https://developers.google.com/adwords/api/docs/guides/authentication)

## How to generate OAuth token and Tdlib session

- Download the jar from releases or [build](#how-to-build) yourself.

- Place the [credentials.json](#how-to-get-credentials.json) and the fat jar in the same directory

- Run the jar

    `java -jar <name_of_the_jar>.jar`

- Follow the instructions present in your screen.

- After completing all the steps, you will get two folders named `tokens` and `tdlib`

## How to make direct link to the credentials

- After [generating the token/session file](#how-to-generate-oauth-token-and-tdlib-session)

- Upload the following files to somewhere convenient for you and get the direct link
  - `StoredCredential` from `tokens` folder

  - `td.binlog` from `tdlib` folder

  - `credentials.json` - Recommended to use [Github gist](https://gist.github.com)

## How to deploy

### Heroku

- Fork this repo.

- Go to [heroku](https://heroku.com) and create an account (skip this if you have an account already)

- Add values for [configs](#configs) as [Github Repository secrets](https://docs.github.com/en/actions/reference/encrypted-secrets)

- Go to Actions tab in the repository, Click Deploy CI present below the all workflows section.

- On the right side, You will see a run workflow spinner. Click that and select `main` branch and click run workflow.

- Wait for 10 min, You will see a green tick. Finally, Bot deployed.
(Make sure you have enabled the dyno in heroku for this app)

## Contributions / Suggestions

Feel free to open a [Pull Request](https://github.com/Dhina17/DhinaBot/pulls) if you want to fix/update anything.

Feel free to create an [issue](https://github.com/Dhina17/DhinaBot/issues) if you have encounter any issues or want to tell some suggestions.

## Credits

- [Rubenlagus](https://github.com/rubenlagus) for his awesome [telegram bot library](https://github.com/rubenlagus/TelegramBots)

- [Cavallium](https://github.com/Cavallium) for his awesome [tdlib java wrapper](https://github.com/tdlight-team/tdlight-java)

- Google for their [DriveAPI](https://developers.google.com/drive/api/v3).

## License

```

    DhinaBot - A simple telegram bot for my personal use
    Copyright (C) 2020-2021  Dhina17 <dhinalogu@gmail.com>
    
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see <http://www.gnu.org/licenses/>.

```
