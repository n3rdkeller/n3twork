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
in:
``` json
{
    "session": "sessionID"
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

