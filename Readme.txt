ssh root@49.12.65.107

ps aux | grep java

copy file to server
scp C:\Users\hartmann\Downloads\geckodriver-v0.28.0-linux64.tar.gz root@49.12.65.107:.\gecko\
scp C:\Users\hartmann\Desktop\Sammelbecken\MarsChatBot\target\chatbot-1.2.0.jar root@49.12.65.107:.\mars\

copy folder to server
scp -r C:\Users\hartmann\Desktop\Sammelbecken\MarsChatBot\target\ root@49.12.65.107:.\mars\



tmux
strg+b dann d