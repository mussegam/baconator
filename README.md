# baconator

See all the bacon lovers around the world!

Plots in a world map the tweets that mention the word bacon and have geoposition information.
A basic Clojure web app using ring/compojure for the backend and Clojurescript for the client.
A small sunday project I coded for ClojureCup2013.

## Usage

To use twitter's Streaming API you need to register an app to obtain the API keys.
Once you have them, create twitter.edn in config/ to store a map like:
```
{:consumer-key "BLABLABLA" 
:consumer-secret "BLABLABLA"
:token "BLABLABLA"
:token-secret "BLABLABLA"}
```
Then run normally with:

lein run

## License

Copyright © 2013 Javier Dolcet

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
