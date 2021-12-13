ssh root@49.12.223.0

ps aux | grep java

copy file to server
scp C:\Users\Laura\Downloads\signal-cli-0.10.0.tar.gz root@49.12.223.0:.\signal\
scp C:\Users\Laura\Entwicklung\MarsChatBot\target\chatbot-2.0.0-jar-with-dependencies.jar root@49.12.223.0:.\mars\

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
  - e.g. ```wsl ~/signal-cli/signal-cli-0.10.0/bin/signal-cli```
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