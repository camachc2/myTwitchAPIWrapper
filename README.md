# myTwitchAPIWrapper
My Twitch API Wrapper 


## Prerequesites:
1. Java API for JSON Processing json-simple
2. Twitch API Dashboard Information
    ie. Client-ID, Client-Secret, Callback URI


## Functionality
1. getStreamersPlaying(gameID) - information of current livestreamers playing gameID
2. getAppAccessToken()         - get app access token
3. getStreamerID(streamerName) - get the streamerID by streamer name
4. clipStreamerNow(broadcasterID, userToken) - clips streamer if they are live
5. validateAuthToken(token)    - information about tokens
6. openUserTokenAuthWebsite()  - opens Auth website for tokens that require user authentication


