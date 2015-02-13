# Introduction

This is the client side of **n3twork**.

*In order to test the client the server side API must be running and reachable from the browser you are going to run the client from!*

# Table of Contents
[TOC]


# Build instructions
1. Install `npm` ([Node Package Manager][1]).
1. Install `bower` ([Bower Package Manager][2]).
1. Install `grunt-cli` ([Grunt JavaScript Task Runner][3]).
1. Run `bower install` to install all needed dependencies specified in `bower.json`.
1. Run `npm install` to install all needed dependencies specified in `package.json`.
1. Run `grunt dist` to minify and copy all templates, stylesheets and scripts. This is simply the compiling process.
1. Link `dist` to your local webserver (and make it accessible to it, talking about rights and stuff).
1. Open it (or containing `index.html`) in your browser.
1. Have fun with `n3twork`!

[1]: https://www.npmjs.org
[2]: http://bower.io
[3]: http://gruntjs.com/


# Project Structure
```text
client
├── src/
|   ├── bower_components/
|   |   ├── angular/
|   |   ├── bootstrap/
|   |   └── ... (third party components are here)
|   |
|   ├── app/
|   |   ├── auth/
|   |   ├── conversation/
|   |   ├── conversations/
|   |   ├── feed/
|   |   ├── friends/
|   |   ├── groups/
|   |   ├── profile/
|   |   ├── register/
|   |   ├── search/
|   |   ├── settings/
|   |   ├── system.APISvc/
|   |   ├── system.CacheSvc/
|   |   ├── system.CommentSvc/
|   |   ├── system.ConversationSvc/
|   |   ├── system.PostSvc/
|   |   ├── system.UserSvc/
|   |   ├── system.VoteSvc/
|   |   ├── views/
|   |   ├── app.filter.js
|   |   └── app.js
|   |
|   ├── css/
|   ├── fonts/
|   ├── img/
|   ├── js/
|   ├── less/
|   └── index.html
|
├── dist/ (compiled project)
|   ├── app/
|   ├── css/
|   ├── js/
|   ├── fonts/
|   └── index.html
|
├── node_modules/
|   ├── grunt/
|   ├── ...
|   └── some other grunt-modules
|
├── bower.json
├── .bowerrc
├── Gruntfile.js
├── package.json
└── README.md
```

# External Dependencies
- [Bootstrap][1]
	- Bootstrap is a collection of HTML templates and stylesheets to use in website design. Most of our designing components and our stylesheet are built with this. It's a very easy to use and nice looking HTML UI framework.
- [AngularJS][2]
	- It's a JavaScript web application framework
- [AngularJS Styleguide][3]
	- Self explanatory (a styleguide for `AngularJS` applications).
- [FontAwesome][4]
	- A nice looking icon font, fully compatible with `Bootstrap`.
- [AngularJS Bootstrap UI][5]
	- A collection of `AngularJS` directives using `Bootstrap` templates.
- [angular-bootstrap-show-errors][6]
	- An `AngularJS` directive to display form validation stuff nicely.
- [angular-scroll-glue][7]
	- An `AngularJS` directive that is automatically scrolling to the bottom of a container it is applied to when it's `$scope` changes.
- [angular-loading-bar][8]
	- An interceptor for `AngularJS`s `$http` requests, displaying a nice loading bar. We used most of this code but customized it for our needs.

[1]: http://getbootstrap.com
[2]: https://angularjs.org
[3]: https://github.com/johnpapa/angularjs-styleguide
[4]: http://fortawesome.github.io/Font-Awesome/
[5]: http://angular-ui.github.io/bootstrap/
[6]: https://github.com/paulyoder/angular-bootstrap-show-errors
[7]: https://github.com/Luegg/angularjs-scroll-glue
[8]: https://github.com/chieffancypants/angular-loading-bar

# Overview
## `src/app/`
### `app.js` and core modules
The `app.js` is the core of the `AngularJS` app.
Here are all modules registered and also the `$routeProvider` which is the AngularJS provider responsible for the routing and displaying different templates in the application's main view.

- `ngRoute`
	- Angulars built-in module for easier and advanced routing
- `ui.bootstrap`
	- AngularJS Bootstrap UI (see [above](#external-dependencies))
- `ui.bootstrap.showErrors`
	- angular-bootstrap-show-errors (see [above](#external-dependencies))
- `luegg.directives`
	- angular-scroll-glue (see [above](#external-dependencies))
- `n3twork.register`
	- handles the registration form
- `n3twork.auth`
	- handles the login form
- `n3twork.profile`
	- handles the display of the profile page
- `n3twork.settings`
	- handles displaying and updating of the users settings
- `n3twork.search`
	- handles the user- and group search
- `n3twork.friends`
	- handles the friends / friend requests / friend suggestions
- `n3twork.groups`
	- handles groups and group display and action
- `n3twork.feed`
	- handles display of the feed
- `n3twork.conversations`
	- handles the list of conversations
- `n3twork.conversation`
	- handles one conversation

### `app.filter.js`

Contains the following filters:

- `symbolForKey`
	- used in the profile to get the symbol for the property key
- `isEmpty`
	- checks if an object is empty, because JavaScript doesn't have this built-in
- `parseDescription`
	- parses the group description for bad html tags and edits images and links
- `shortenDescription`
	- similar to `parseDescription`, but shortens the text
- `parsePost`
	- similar to `parseDescription`, but for posts
- `convertToDate`
	- converts a unix timestamp to the local date-and-time-representation
- `convertToAgo`
	- converts a unix timestamp to a `20s ago` string

### `src/app/auth`
This handles the login form.
Contains the controller `AuthCtrl`.

### `src/app/conversation`
This handles one conversation.

Contains:

- the directive `conversationMessages`
- the controller `ConversationCtrl`
- the view `conversation.html`
- the send `angular-loading-bar` (modified 3rd party module) `conversation.loadingbar.js`

### `src/app/conversations`
This handles the conversation-list.

Contains:

- the controller `ConversationsCtrl`
- the view `conversations.html`
- `conversationsButton` (shows unread-count and triggers reload-event)
	- the directive `conversationsButton`
	- the controller `ConversationsButtonCtrl`
	- the view `conversationsButton.html`

### `src/app/feed`
This handles the news feed.
Contains the controller `FeedCtrl` and the view `feed.html`

### `src/app/friends`
This handles the friend and friend request and friend suggestions list.
Contains the controller `FriendsCtrl` and the view `friends.html`.

### `src/app/groups`
This handles the groups overview, the display of one group, and the group creation.

- Groups Overview
	- the controller `GroupsCtrl`
	- the view `groups.html`
	- the modal to create a group
		- the controller `CreateGroupCtrl`
		- the view `createGroupModal.html`

- Group (one group)
	- the controller `GroupCtrl`
	- the view `group.html`
	- the modal to confirm leaving if user is the last one in the group
		- the controller `LeaveConfirmationCtrl`
		- the view (`ng-template` at the end of `group.html`) `groupLeaveConfirmation.html`

- Members
	- the controller `MembersCtrl`
	- the view `members.html`

### `src/app/profile`
This handles the profile page.
Contains the controller `ProfileCtrl` and the view `profile.html`.

### `src/app/register`
This handles the registration form.
Contains the controller `RegisterCtrl` and the view `register.html`.
Uses Angulars `ngMessages` directive to display form validation error messages.

### `src/app/search`
This handles the search form.
Contains the controller `SearchCtrl` and the view `search.html`.

### `src/app/settings`
This handles the settings form.
Contains the controller `SettingsCtrl` and the view `settings.html`.

### `src/app/views`
This contains some globally used views.

### Services and Factories
To learn the difference between a `service` and a `factory`, take a look at [this](http://stackoverflow.com/a/15666049/2386909).
They could have been used not entirely right in some cases here.

#### `src/app/system.APISvc`
This is a `factory`.
Handles all communications to and from the API via Angulars `$http` service.
Also applies the `session` key to all communication.

#### `src/app/system.CacheSvc`
This is a `service`.
Handles and caches some of the API resources, like the search or the friendlist.
There is a more angular-way to do this, but we hadn't figured it out at the time.

It also uses the `APISvc` itself instead of just acting like a middleware between a controller and another service / factory e.g. the `FriendSvc`.

#### `src/app/system.CommentSvc`
This is a `factory`.
Handles all communication related to the comments.

#### `src/app/system.ConversationSvc`
This is a `service`.
Handles all communcation related to the conversations.

#### `src/app/system.PostSvc`
This is a `factory`.
Handles all communcation related to the posts.

#### `src/app/system.UserSvc`
This is a `service`.
Handles all communcation related to the user.

#### `src/app/system.VoteSvc`
This is a `service`.
Handles all communcation related to the votes.

## `src/css/`
Contains files linked to the minified css files of the used frameworks.

## `src/fonts/`
Contains files of the used fonts.

## `src/img/`
Contains used images.

## `src/js/`
Contains files linked to the (minified) JavaScript files of the used frameworks and libraries.
Grunt compiles them into `dist/js/n3twork.min.js`.

## `src/less/`
Contains own stylesheets written in `LESS` ([LESS CSS Preprocessor](http://lesscss.org)) and compiled into `dist/css/main.css` by Grunt later.

## `src/index.html`
The index HTML template. Here happens all the action.
