var app = new Vue({
  el: '#app',
  data: {
    loggedIn: false,
    username: "Martin",
    EB: null,
    chatParty: null,
    availableChatParties : ['Martin', 'Joe', 'Kaka Tonka'],
    messages : {
        Martin: [{text: 'Hi'}, {text: 'Whazzzuuuppp'}],
        Joe : [{text: 'Can you please implement this feature till the end of the month???'}, {text: 'Sure'}],
        'Kaka Tonka' : []
    },
    inputMessage : ''
  },
  methods: {
    login: function() {
      this.EB = new EventBus("http://localhost:8080/eventbus");

      var ebInstance = this.EB;
      var username = this.username;
      var t = this;

      this.EB.onopen = function() {

        console.log("Registering loginresult handler...");
        ebInstance.registerHandler("loginresult." + username, function(error, message) {
          console.log('received a message: ' + JSON.stringify(message));
          if (message.body) {
            if ('success' === message.body.login) {
              t.loggedIn = true;
            }
          }
        });

        ebInstance.registerHandler("message.send." + username, function(error, message) {
            console.log('Message was successfully send: ' + JSON.stringify(message));
        });

        ebInstance.registerHandler("message.receive." + username, function(error, message) {
            console.log('Message received: ' + JSON.stringify(message));
            if (message.body.sender) {
                t.messages[message.body.sender].push(message.body);
            }
        });

        console.log("Try to login with: " + username);
        ebInstance.send("login", {username: username});
      };
    },

    logout: function() {
        this.username = null;
        this.loggedIn = false;
    },

    selectChatParty: function(party) {
      console.log('SELECTING A CHAT PARTY - ' + party);
      this.chatParty = party;
    },

    isChatPartyActive: function(party) {
       return party === this.chatParty;
    },

    sendMessage: function() {
        console.log('Sending message ' + this.inputMessage + ' TO ' + this.chatParty);
        var message = {sender: this.username, receiver: this.chatParty, text: this.inputMessage};
        this.messages[this.chatParty].push(message);
        this.EB.send("message", message);
        this.inputMessage = '';
    }
  }
});
