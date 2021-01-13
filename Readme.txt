ssh root@49.12.65.107


copy file to server
scp C:\Users\hartmann\Desktop\testfile.txt root@49.12.65.107:.
scp C:\Users\hartmann\Downloads\geckodriver-v0.28.0-linux64.tar.gz root@49.12.65.107:.\gecko\

copy folder to server
scp -r C:\Users\hartmann\Desktop\Sammelbecken\MarsChatBot\target\ root@49.12.65.107:.\mars\



export FIREFOX_PATH=/gecko/


ps aux | grep java


nohup java -jar mars/chatbot-1.1.1.jar

tmux
strg+b dann d