How to play chat?

1)open command line in "Chat Entity" folder and type: `mvn clean package install`

2)start server from command line: `java ServerDemo [args]`

args can be empty
or you can define port number: args[0] - tcp port number

3)start clients : `java ClientDemo [args]`

args can be empty
or you can define:

- args[0] - server host , by default "localhost"

- args[1] - client nickname

- args[2] - server tcp port number


4)type messages, all clients who have already logged in (started) will see your messages

5)additional info:

- type `LOG_OUT` to logout and disconnect.

- type `WHO_IS_ONLINE` to discover who is online at the moment.

- type `WEATHER_MINSK` to get Minsk weather forecast.
