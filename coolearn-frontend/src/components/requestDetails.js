import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = 'http://' + config.dev.backendHost + ':' + config.dev.backendPort

var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    name: 'RequestDetails',
    data() {
        return {
            req: {},
            comments: [],
            review_id: [],
            sessions_list: [],
            
        }
    },
    created: function () {
        // Initializing people from backend
        //Get userRoleid
        let userRole_id=this.getUserRoleId();
        this.req=JSON.parse(localStorage.getItem('current_request'));
        console.log(this.req);
        console.log(this.req.studentIds);
        for (var i=0; i<this.req.studentIds.length;i++){
            this.getComments(userRole_id, this.req.studentIds[i]);
        }
        // this.getComments(userRole_id, this.req.studentIds[0]);
        
        //Get reviews by userRole
        for (var i = 0; i < this.req.studentIds.length; i++) {
            this.getComments(userRole_id, this.req.studentIds[i]);
        } 
    },

    methods:{
        getUserRoleId: function() {
            let id = localStorage.getItem('userRoleId')
            if (id == null) {
                this.logout()
                throw new Error("There is no user logged in")
            } else {
                return id
            }
        },
        logout: function() {
          localStorage.clear();
          this.$router.push('login')
        },
        goBack: function(){
            localStorage.removeItem('current_request');
            this.$router.replace({ path: `/dashboard` });
        },
        getComments: function (userRole_id_tutor, userRole_id_student) {
            AXIOS.get(`/dashboard/${userRole_id_tutor}/${userRole_id_student}`)
                .then(response => {
                    // JSON responses are automatically parsed.
                    this.comments = response.data;
                    this.comments.forEach((rating) => {
                        this.review_id.push(rating.sessionId);
                    });

                    for(var i=0; i < this.review_id.length; i++) {
                        this.getSessionById(this.review_id[i]);
                    }
                    
                })
                .catch(e => {
                    console.log(e);
                });
        },
        getUserRoleId: function () {
            let id = localStorage.getItem('userRoleId')
            if (id == null) {
                this.logout()
                throw new Error("There is no user logged in")
            } else {
                return id
            }
        },
        doSlideshow: function () {
            var slideIndex = 0;
            var i;
            var x = document.getElementsByClassName("slideshow");
            for (i = 0; i < x.length; i++) {
                x[i].style.display = "none";
            }
            slideIndex++;
            if (slideIndex > x.length) { slideIndex = 1 }
            x[slideIndex - 1].style.display = "block";
            setTimeout(carousel, 2000); // Change image every 2 seconds
        },
        getSessionById: function(session_id) {
            AXIOS.get(`/getSession/${session_id}`)
                .then(response => {
                    // JSON responses are automatically parsed.
                    this.sessions_list.push(response.data);
                   
                })
                .catch(e => {
                    console.log(e);
                });
        },
        calculateStart: function(number) {
            var closestInteger = Math.round(number);
            // can be implemented
        }
    }
}    