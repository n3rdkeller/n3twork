# Server
This is the server side of our n3twork.

## Table of Contents
- [Introduction](#introduction)
- [classes](#classes)
    - [User](#user)
- [servlet](#servlet)
    - ...
- [API](#n3twork-api-quick-reference)

## Introduction
We used eclipse to generate a `n3.war` we deployed on our Tomcat 8 Server.

The server is seperated into two parts, as is indicated by the directory structure: classes, which contains the classes, whose objects are used, and servlet, which contains classes of the REST API.

## classes
The class `Main` is only used for testing. 

### User
This class is central to *n3twork*, which is to be expected from a social network. Most of the db queries are found here. An object of this class represents a user in the network. The most used attributes are:
- `id` is a unique integer and analog to the `id` column in the `Users` table
- `name` and `firstName` describe the name of the user
- `username` is a unique String and analog to the `username` column in the `Users` table
- `email` should also be a unique String, but is only used on login as a identifier
- `password` is allways hashed with md5
- `sessionID` is a md5 hash of the username with the current time in milliseconds concatenated
- `otherProperties` is a HashMap of all other attributes a user could have (e.g. city, bio, workplace). This is implemented dynamically: support for a new property only has to be added to the client and the db

The feature of friends is also implemented in `User`. It uses two attributes:
- `friends` is a `HashMap<User, SimpleEntry<Long, Boolean>>`
    - the key is the user object of the friend
    - the key of the `SimpleEntry` is the timestamp when the connection was established
    - the value of the `SimpleEntry` is true if the connection exists both ways
- `friendRequests` is a `HashMap<User, Long>`
    - the key is the user object of the friend request
    - the value is the timestamp of the time the friend request was made

## Group
`Group` is part of the implementation of the groups feature. An object of the class represents a group with the following attributes:
- `id` is a unique identifier, related to the `id` column in the `Groups` table
- `name` and `descr` describe the group
- `members` is a list of `User` objects
- `memberCount` is needed to save the lenght of `members` without having the list
- `otherProperties` is equal to the correspondent attribute in [User](#user)
- `posts` see [Post](#post)

A lot of the user related methods are found in `User`

## Post
Objects of this class represents posts of users on their profil or in a group. The attibutes are:
- `id` is a unique identifier, related to the `id` column in the `Posts` table
- `owner` is a group
- `author` is the user, who wrote the post
- `content` explains itself
- `postDate` is the date of the creation of the post
- `privatePost` is true if the post should only be visual to friends of the `author`. This is irrelevant, if `goupPost` is true.
- `groupPost` is true if the post is in a group and false if it should be displayed in the profil. If it is false, `owner` is ignored
- `upvotes` is a `HashMap<User,Date>`. The key is the voter and the value the date of voting
- `numberOfUpVotes` is needed to save the size of `upVotes` without having the Map
- `didIVote` is true if the spectating user has already voted
- `comments` is a `HashMap<SimpleEntry<User,Integer>,SimpleEntry<String,Date>>`, which indicates, that this should've been a seperate class
    - the key of the key is the commentator
    - the value of the key is the comment id (from `Comments` table in db)
    - the key of the value is the content
    - the value of the key is the date of creation
- `numberOfComments` is needed to save the size of `comments` without having the Map

Methods implementing an action by a user are usually in `User`

## n3twork API Quick Reference
- [POST /login](#login)
- [POST /logout](#logout)
- [POST /register](#register)
- [POST /register/checkuser](#register-checkuser)
- [POST /user](#user)
- [PUT /user/settings](#user-settings)
- [POST /user/remove](#user-remove)
- [POST /user/find](#user-find)
- [POST /user/count](#user-count)
- [POST /user/friends](#user-friends)
- [POST /user/friendrequests](#user-friendrequests)
- [POST /user/friend/add](#user-friend-add)
- [POST /user/friend/remove](#user-friend-remove)
- [POST /user/groups](#user-groups)
- [POST /user/group/join and POST /user/group/leave](#user-group-join-and-user-group-leave)
- [POST /group/create](#group-create)
- [POST /group/show](#group-show)
- [POST /group/find](#group-find)
- [POST /group/count](#group-count)
- [POST /group/members](#group-members)
- [POST /post](#post)
- [POST /post/newsfeed](#post-newsfeed)
- [POST /post/votes](#post-votes)
- [POST /post/add](#post-add)
- [PUT /post/update](#post-update)
- [POST /post/delete](#post-delete)
- [POST /post/vote/add and POST /post/vote/remove](post-vote-add-and-post-vote-remove)
- [POST /post/comments](#post-comments)
- [POST /post/comment/add](#post-comment-add)
- [POST /post/comment/remove](#post-comment-remove)
- [POST /conversation/](#conversation)
- [POST /conversation/show](#conversation-show)
- [POST /conversation/send](#conversation-send)
- [POST /conversation/new](#conversation-new)
- [POST /conversation/archive](#conversation-archive)
- [POST /conversation/unread](#conversation-unread)
- [POST /conversation/rename](#conversation-rename)
- [POST /suggestion/network](#suggestion-network)
- [POST /suggestion/post](#suggestion-post)

### /login
#### POST
in:
``` json
{
    "login" : "username/email",
    "password" : "pw in plain text"
}
```
out:
``` json
{
    "session":"sessionID",
    "id":0,
    "username":"username",
    "email":"email",
    "lastname":"last name",
    "firstName":"first name",
    "otherProperties":{
        "propertie1":"value",
        "propertie2":"value",
    },
    "successful":true
}
```
### /logout
#### POST
in:
``` json
{
    "session" : "sessionID"
}
```
out:
``` json
{
    "successful":true
}
```
### /register
#### POST
in:
``` json
{
    "email":"email@text",
    "password":"pw as plain text",
    "username":"usernametext"
}
```
out:
``` json
{
    "successful":true
}
```
### /register/checkuser
#### POST
in:
``` json
{
    "username":"usernametext"
}
```
out:
``` json
{
    "username":"usernametext",
    "taken":true false,
    "successful":true
}
```
### /user
#### POST
in:
``` json
{
	"session":"sessionID",
	"id":1337 "//userID: Optional (Wenn keine userID gegeben ist wird aktueller user genommen)"
}
```
out:
``` json
{
    "email": "zwerch1337@gmail.com",
    "firstName": "Robin",
    "id": 45,
    "lastname": "Temme",
    "otherProperties": {},
    "session": "",
    "successful": true,
    "username": "zwerch"
}
```
### /user/remove
#### POST
in:
``` json
{
	"session":"sessionID"
}
```
out:
``` json
{
    "successful": true
}
```
### /user/find
#### POST
in:
``` json
{
"session":"sessionID"
}
```
out:
``` json
{
    "successful": true,
    "userList": [
        {
            "email": "asd@asd.de",
            "firstName": "",
            "id": 29,
            "lastname": "",
            "otherProperties": {},
            "username": "asdddddd"
        },
        {
            "email": "theres...neider@gmail.com",
            "firstName": "",
            "id": 30,
            "lastname": "",
            "otherProperties": {},
            "username": "Tessa1337"
        },
    ]
}
```
### /user/count
#### POST
in:
``` json
{
    "session": "sessionID"
}

```
out:
``` json
{
    "users": 11,
    "usersOnline": 3
}
```

### /user/friends
#### POST
in:
``` json
{
	"session":"sessionID",
	"id":45 "//userID: Optional (Wenn keine userID gegeben ist wird aktueller user genommen)"
}
```
out:
``` json
{
    "friends": [
        {
            "date": 1417123181000,
            "email": "zwerch1337@gmail.com",
            "firstName": "Robin",
            "id": 45,
            "lastname": "Temme",
            "trueFriend": false,
            "username": "zwerch"
        },
        {
            "date": 1417122900000,
            "email": "horst@dieter.ded",
            "firstName": "",
            "id": 42,
            "lastname": "",
            "trueFriend": false,
            "username": "ddasddddddasdasdasd"
        },
    ],
    "successful": true
}
```
### /user/friendrequests
#### POST
in:
``` json
{
    "session": "sessionID",
    "id":45 "//userID: Optional (Wenn keine userID gegeben ist wird aktueller user genommen)"
}
```
out:
``` json
{
    "friendRequests": [
        {
            "date": 1417208589000,
            "email": "dieter@rasse.de",
            "firstName": "Dieter",
            "id": 44,
            "lastname": "Rasse",
            "trueFriend": false,
            "username": "dieter"
        },
    ],
    "successful": true
}
```
### /user/friend/add
#### POST
in:
``` json
{
	"session":"sessionID",
	"friend":1337
}
```
out:
``` json
{
    "successful": true
}
```
### /user/friend/remove
#### POST
in:
``` json
{
	"session":"sessionID",
	"friend":1337
}
out:
``` json
{
    "successful": true
}
```
### /user/groups
#### POST
in:
``` json
{
	"session":"sessionID",
	"id":45 "//userID: Optional (Wenn keine userID gegeben ist wird aktueller user genommen)"
}
```
out:
``` json
{
    "groups": [
        {
            "groupDescr": "Beschreibung",
            "groupID": 2,
            "groupName": "Coole Gruppe"
        }
    ],
    "successful": true
}
```
### /user/group/join and /user/group/leave
#### POST
in:
``` json
{
	"session":"sessionID",
	"group":1337
}
```
out:
``` json
{
    "successful": true
    "//Bei join auch wenn der user schon Mitglied ist. Wird aber nicht 2x in der db registriert"
}
```
### /group/create
#### POST
in:
``` json
{
    "groupDescr": "Beschreibung",
    "groupName": "Name",
    "session": "sessionID"
}
```
out:
``` json
{
    "successful": true
}
```
### /group/show
#### POST
in:
``` json
{
	"session":"sessionID",
	"group":1337
}
```
out:
``` json
{
    "descr": "Beschreibung",
    "id": 2,
    "name": "Name",
    "otherProperties": {},
    "successful": true
}
```
### /group/find
in:
``` json
{
	"session":"sessionID"
}
```
out:
``` json
{
    "groups": [
        {
            "groupDescr": "Beschreibung",
            "groupID": "2",
            "groupName": "Coole Gruppe"
        },
        {
            "groupDescr": "Beschreibung",
            "groupID": "2",
            "groupName": "Coole Gruppe"
        },
    ],
    "successful": true
}
```
### /group/count
#### POST
in:
``` json
{
    "session": "sessionID"
}
```
out:
``` json
{
    "groups": 5
}
```
### /group/members
#### POST
in:
``` json
{
	"session":"sessionID",
	"group":1337
}
```
out:
``` json
{
    "members": [
        {
            "email": "dieter@rasse.de",
            "firstName": "Dieter",
            "id": 44,
            "name": "Rasse",
            "username": "dieter"
        },
        {
            "email": "johannes@n3rdkeller.de",
            "firstName": "",
            "id": 47,
            "name": "",
            "username": "johannes"
        },
    ],
    "successful": true
}
```
### /post
#### POST
in:
``` json
{
    "groupID":0, "//optional if given uses group"
    "userID":0, "//optional if given uses user"
    "session":"sessionID"
}
```
out:
``` json
{
    "postList": [
        {
            "author":authorID number,
            "content":"content text",
            "id":postID number,
            "owner":ownerID number,
            "postDate":timestamp number,
            "upVotes": [
                {
                    "date":timestamp number,
                    "voter":voterID number
                },
            ],
            "private":true/false
        },
    ],
    "successful":true
}
```
### /post/newsfeed
#### POST
in:
``` json
{
    "session":"sessionID"
}
```
out:
``` json
{
    "postList": [
        {
            "author":authorID number,
            "content":"content text",
            "id":postID number,
            "owner":ownerID number,
            "postDate":timestamp number,
            "upVotes": [
                {
                    "date":timestamp number,
                    "voter":voterID number
                },
            ],
            "private":true/false
        },
    ],
    "successful":true
}
```
### /post/votes
#### POST
in:
``` json
{
    "id":postID number,
    "session":"sessionID"
}
```
out:
``` json
{
    "voteList": [
        {
            "date":voteDate number,
            "voter":{
                "firstName":firstName text,
                "name":name text,
                "username":username text
            }
        },
    ],
    "successful":true
}
```
### /post/add
#### POST
in:
``` json
{
    "groupID":0, "//optional if given uses group"
    "userID":0, "//optional if given uses user"
    "session":"sessionID",
    "post": {
        "content":"",
        "private":true false
    }
}
```
out:
``` json
{
    "successful":true
}
```
### /post/delete
#### POST
in:
``` json
{
    "session":"sessionID",
    "id":0 "//id of the doomed post"
}
```
out:
``` json
{
    "successful":true
}
```
### /post/vote/add and /post/vote/remove
#### POST
in:
``` json
{
    "id":postID,
    "session":"sessionID"
}
```
out:
``` json
{
    "successful":true
}
```
### /post/comments
#### POST
in:
``` json
{
    "id":"postID",
    "session":"sessionID"
}
```
out:
``` json
{
    "commentList":[
        {
            "author":{
                "firstName":"firstName text",
                "lastname":"lastname text",
                "username":"username text"
            },
            "content":"content text",
            "date":0, "//comment date as unix timestamp"
            "id":0 "//commentID"
        },
    ],
    "successful":true
}
```
### /post/comment/add
#### POST
in:
``` json
{
    "id":0, "//postID"
    "content":"contentOfComment text",
    "session":"sessionID"
}
```
out:
``` json
{
    "successful":true
}
```
### /post/comment/remove
#### POST
in:
``` json
{
    "id":0, "//commentID"
    "session":"sessionID"

}
```
out:
``` json
{
    "successful":true
}
```
### /conversation
#### POST
in:
``` json
{
    "session":"sessionID"
}
```
out:
``` json
{
    "conversationList": [
        {
            "receiverList": [
                {
                "username":"",
                "firstName":"",
                "lastName":"",
                "email":"",
                "emailhash":""
                },
            ]
            "lastread":0,
            "name":"",
            "id":0
        },
    ],
    "successful":true
}
```
### /conversation/show
#### POST
in:
``` json
{
    "session":"sessionID",
    "conversationID":0, 
    "lastread":0
}
``` 
out:
``` json
{
    "messageList":[
        {
            "content":"content",
            "senderDate":456456465465,
            "senderID":0
        },
    ],
    "successful":true
}
```
### /conversation/send
#### POST
in:
``` json
{
    "session":"sessionID",
    "content":"asdfasdf",
    "conversationID":0
}
```
out:
``` json
{
    "successful":true,
    "id":0 //message id
}
```
### /conversation/new
#### POST
in:
``` json
{
    "session":"sessionID",
    "name":"conName", //optional
    "receiverList":[
        {
            "username":"username"
        },
    ]
}
```
out:
``` json
{
    "successful":true,
    "conversationID":0
}
```
### /conversation/archive
#### POST
in:
``` json
{
    "session":"sessionID",
    "conversationID":0
}
```
out:
``` json
{
    "successful":true
}
```
### /conversation/unread
#### POST
in:
``` json
{
    "session":"sessionID"
}
```
out:
``` json
{
    "unread":0,
    "successful":true
}
```
### /conversation/rename
#### POST
in:
``` json
{
    "session":"sessionID",
    "id":0,
    "name":"new name"
}
```
out:
``` json
{
    "successful":true
}
### /suggestion/network
#### POST
in:
``` json
{
    "session":"sessionID"
}
```
out:
```
{
    "userList": [
        {
            "date": 1417123181000,
            "email": "zwerch1337@gmail.com",
            "firstName": "Robin",
            "id": 45,
            "lastname": "Temme",
            "trueFriend": false,
            "username": "zwerch"
        },
        {
            "date": 1417122900000,
            "email": "horst@dieter.ded",
            "firstName": "",
            "id": 42,
            "lastname": "",
            "trueFriend": false,
            "username": "ddasddddddasdasdasd"
        },
    ],
    "successful": true
}
```
### /suggestion/post
#### POST
in:
``` json
{
    "session":"sessionID"
}
```
out:
```
{
    "userList": [
        {
            "date": 1417123181000,
            "email": "zwerch1337@gmail.com",
            "firstName": "Robin",
            "id": 45,
            "lastname": "Temme",
            "trueFriend": false,
            "username": "zwerch"
        },
        {
            "date": 1417122900000,
            "email": "horst@dieter.ded",
            "firstName": "",
            "id": 42,
            "lastname": "",
            "trueFriend": false,
            "username": "ddasddddddasdasdasd"
        },
    ],
    "successful": true
}
```