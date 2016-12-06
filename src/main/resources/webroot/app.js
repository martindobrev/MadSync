var app = new Vue({
  el: '#app',
  data: {
    loggedIn: false,
    username: "",
    EB: null,
    chatParty: null,
    availableChatParties : ['Martin', 'Joe', 'Kaka Tonka']
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

        console.log("Try to login with: " + username);
        ebInstance.send("login", {username: username});
      };
    },

    selectChatParty: function(party) {
      console.log('SELECTING A CHAT PARTY - ' + party);
      this.chatParty = party;
    },

    isChatPartyActive: function(party) {
       return party === this.chatParty;
    }
  }
});
