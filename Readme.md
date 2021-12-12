ssh root@49.12.65.107

ps aux | grep java

copy file to server
scp C:\Users\hartmann\Downloads\geckodriver-v0.28.0-linux64.tar.gz root@49.12.65.107:.\gecko\
scp C:\Users\hartmann\Desktop\Sammelbecken\MarsChatBot\target\chatbot-1.2.2.jar root@49.12.65.107:.\mars\

copy folder to server
scp -r C:\Users\hartmann\Desktop\Sammelbecken\MarsChatBot\target\ root@49.12.65.107:.\mars\



tmux
strg+b dann d

## Environment Configuration
The app is configured by Env Variables that are read on startup. 

###mandatory
- SIGNAL_USERNAME
  - eg ```+4911111111```
- SIGNAL_CLI_PATH
  - e.g. ```wsl ~/signal-cli/signal-cli-0.9.2/bin/signal-cli```
- SIGNAL_SEND_GROUP
  - e.g. ```jUBrrj4YVbGdz...F6U\=```
  
### optional 
- SIGNAL_CONFIG_GROUP
  - e.g. ```jUBrrj4...F6U\=```
- TM_GAME_URL
  - e.g. ```http://...```

## usage of config Groups
you can send commands to the config group
Commands consist of 2 parts, seperated by a single space:
```[commandName] [parameter]```

Currently Supported Commands over Config Group:

```setGameUrl http://url...```