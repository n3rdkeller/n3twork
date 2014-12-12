# Server

This is the server side of our n3twork.

We used `eclipse` to generate a `n3.war` we deployed on our Tomcat 8 Server.

# n3twork API Quick Reference
- [POST /login](#login)
- [GET /logout](#logout)
- [POST /register](#register)
- [POST /register/checkuser](#registercheckuser)
- [POST /user](#user)
- [POST /user/settings](#usersettings)
- [GET /user/remove](#userremove)
- [GET /user/find](#userfind)
- [GET /user/count](#usercount)
- [POST /user/friends](#userfriends)
- [POST /user/friendrequests](#userfriendrequests)
- [POST /user/friend/add](#userfriendadd)
- [POST /user/friend/remove](#userfriendremove)
- [POST /user/groups](#usergroups)
- [POST /user/group/join and POST /user/group/leave](#usergroupjoin-and-usergroupleave)
- [POST /group/create](#groupfound)
- [POST /group/show](#groupshow)
- [GET /group/find](#groupfind)
- [GET /group/count](#groupcount)
- [POST /group/members](#groupmembers)
- [POST /post](#post)
- [GET /post/newsfeed](#postnewsfeed)
- [POST /post/votes](#postvotes)
- [POST /post/add](#postadd)
- [POST /post/update](#postupdate)
- [POST /post/delete](#postdelete)
- [POST /post/vote/add and POST /post/vote/remove](postvoteadd-and-postvoteremove)
- [POST /post/comments](#postcomments)
- [POST /post/comment/add](#postcommentadd)
- [POST /post/comment/remove](#postcommentremove)

#### /login
##### POST
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
        "firstname":"first name",
        "otherProperties":{
            "propertie1":"value",
            "propertie2":"value",
    },
    "successful":true
}
```
#### /logout
##### GET
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
#### /register
##### POST
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
#### /register/checkuser
##### POST
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
#### /user
##### POST
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
    "firstname": "Robin",
    "id": 45,
    "lastname": "Temme",
    "otherProperties": {},
    "session": "",
    "successful": true,
    "username": "zwerch"
}
```
#### /user/remove
##### GET
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
#### /user/find
##### GET
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
            "firstname": "",
            "id": 29,
            "lastname": "",
            "otherProperties": {},
            "username": "asdddddd"
        },
        {
            "email": "theres...neider@gmail.com",
            "firstname": "",
            "id": 30,
            "lastname": "",
            "otherProperties": {},
            "username": "Tessa1337"
        }, 
    ]
}
```
#### /user/count
##### GET
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

#### /user/friends
##### POST
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
            "firstname": "Robin",
            "id": 45,
            "lastname": "Temme",
            "trueFriend": false,
            "username": "zwerch"
        },
        {
            "date": 1417122900000,
            "email": "horst@dieter.ded",
            "firstname": "",
            "id": 42,
            "lastname": "",
            "trueFriend": false,
            "username": "ddasddddddasdasdasd"
        },
    ],
    "successful": true
}
```
#### /user/friendrequests
##### POST
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
            "firstname": "Dieter",
            "id": 44,
            "lastname": "Rasse",
            "trueFriend": false,
            "username": "dieter"
        },
    ],
    "successful": true
}
```
#### /user/friend/add
##### POST
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
#### /user/friend/remove
##### POST
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
#### /user/groups
##### POST
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
#### /user/group/join and /user/group/leave
##### POST
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
#### /group/create
##### POST
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
#### /group/show
##### POST
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
#### /group/find
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
#### /group/count
##### GET
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
#### /group/members
##### POST
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
#### /post
##### POST
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
#### /post/newsfeed
##### GET
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
#### /post/votes
##### POST
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
#### /post/add
##### POST
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
#### /post/delete
##### POST
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
#### /post/vote/add and /post/vote/remove
##### POST
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
#### /post/comments
##### POST
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
                "firstname":"firstname text",
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
#### /post/comment/add
##### POST
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
#### /post/comment/remove
##### POST
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