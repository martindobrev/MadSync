var app = new Vue({
    el: '#app',
    data: {
        loggedIn: false,
        username: "Martin",
        EB: null,
        chatParty: null,
        availableChatParties: ['Martin', 'Joe', 'Kaka Tonka'],
        messages: {
            Martin: [],
            Joe: [],
            'Kaka Tonka': []
        },
        inputMessage: ''
    },
    methods: {
        login: function () {
            this.EB = new EventBus("http://localhost:8080/eventbus");

            var ebInstance = this.EB;
            var username = this.username;
            var t = this;

            this.EB.onopen = function () {
                console.log("Registering loginresult handler...");
                ebInstance.registerHandler("loginresult." + username, function (error, message) {
                    console.log('received a message: ' + JSON.stringify(message));
                    if (message.body) {
                        if ('success' === message.body.login) {
                            t.loggedIn = true;
                        }
                    }

                    t.requestData();
                });

                ebInstance.registerHandler("message.send." + username, function (error, message) {
                    console.log('Message was successfully send: ' + JSON.stringify(message));
                });

                ebInstance.registerHandler("message.receive." + username, function (error, message) {
                    console.log('Message received: ' + JSON.stringify(message));
                    if (message.body.sender) {
                        t.messages[message.body.sender].push(message.body);
                    }
                });

                ebInstance.registerHandler('data.' + username, function(error, message) {
                    console.log('Sync messages received: ' + JSON.stringify(message.body));
                    if (message.body) {
                        for (var messageId in message.body) {
                            var textMessage = message.body[messageId];

                            if (textMessage.sender === t.username) {
                                t.messages[textMessage.receiver].splice(parseInt(messageId) - 1, 0, textMessage);
                            } else {
                                t.messages[textMessage.sender].splice(parseInt(messageId) - 1, 0, textMessage);
                            }
                        }
                    }
                });

                console.log("Try to login with: " + username);
                ebInstance.send("login", {username: username});
            };

            this.EB.onclose = function() {
                console.log('Connection closed!');
            }

        },

        logout: function () {
            this.username = null;
            this.loggedIn = false;
            this.messages = {Martin: [], Joe: [], 'Kaka Tonka': []};
            this.EB.close();
        },

        selectChatParty: function (party) {
            console.log('SELECTING A CHAT PARTY - ' + party);
            this.chatParty = party;
        },

        isChatPartyActive: function (party) {
            return party === this.chatParty;
        },

        sendMessage: function () {
            console.log('Sending message ' + this.inputMessage + ' TO ' + this.chatParty);
            var message = {sender: this.username, receiver: this.chatParty, text: this.inputMessage};
            this.messages[this.chatParty].push(message);
            this.EB.send("message", message);
            this.inputMessage = '';
        },

        isIncomingMessage: function (message) {
            if (message) {
                if (message.sender !== this.username) {
                    return true;
                }
            }
            return false;
        },

        /**
         * Requests the data for the user
         *
         * Necessary to sync the state of the client and the server about
         * previous messages
         */
        requestData: function() {
            var requestData = {
                userId              : this.username,
                maxSequenceNumber   : 0,
                sequence            : {available: false, sequence: []}
            };
            this.EB.send("data", requestData);
        }
    }
});
