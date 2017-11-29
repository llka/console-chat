How to play chat?

1)Download all files from https://git.epam.com/Ilya_Kisel/javalab/tree/console_chat

2)open command line in "Chat Entity" folder and type: mvn clean package install

3)start server from command line: java ServerDemo [args]
args can be empty
or you can define port number
args[0] - tcp port number

4)start clients : java ClientDemo [args]
args can be empty
or you can define:
args[0] - server address , by default "localhost"
args[1] - client nickname
args[2] - server tcp port number

5) type messages, all clients who have already logged in (started) will see your messages

6)additional info:
-type LOG_OUT to logout and disconnect.
-type WHO_IS_ONLINE to discover who is online at the moment.
-type WEATHER_MINSK to get Minsk weather forecast.