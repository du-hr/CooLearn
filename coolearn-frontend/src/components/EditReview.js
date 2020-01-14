import Vue from 'vue';
window.Vue = Vue;
import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = 'http://' + config.dev.backendHost + ':' + config.dev.backendPort

var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    name: 'editReview',
    data() {
        return {
            req:{},
            student:{
                name:"",
                id: Number
            },
            review:{}
        }
    },

    created:function(){
        let userRole_id = this.getUserRoleId();
        this.req=JSON.parse(localStorage.getItem('requestToReview'));
        this.student.name=localStorage.getItem('studentName');
        this.student.id=parseInt(localStorage.getItem('studentId'));
        this.review=JSON.parse(localStorage.getItem('reviewToEdit'));
        console.log(this.review);
    },    
    methods:{

        getUserRoleId: function () {
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

        checkStars: function(rating){
            let stars = document.getElementsByTagName('input');
            console.log(stars);
            var index = 5 - rating;
            for (var i=4; i>=index;i--){

                console.log(stars[i]);
                stars[i].setAttribute('checked','true');
            }
            stars.forEach((e)=> {
                console.log(e);

            });
            
        },

        putReview: function(){
            let userRole_id = this.getUserRoleId();
            let session_id = this.req.id;
            let reviewee_id = this.student.id;
            AXIOS.put(`/dashboard/${userRole_id}/${session_id}/${reviewee_id}/${this.review.id}`, {
                comments:document.getElementsByTagName('textarea')[0].value,
                rating:this.review.rating
              }).then(response => {
                console.log(response.data)
              }).catch(e => {
                alert(e.response.data.message)
              });
              this.goBack();
        },

        goBack: function(){
            localStorage.removeItem('requestToReview');
            localStorage.removeItem('studentName');
            localStorage.removeItem('studentId');
            localStorage.removeItem('reviewToEdit');
            this.$router.replace({path:`/dashboard`});
        },

        getRating:function(){
            let stars = document.getElementsByTagName('input');
            console.log(stars);
            for (var i=0;i<stars.length;i++){
                if (stars[i].checked){
                    return parseFloat(stars[i].value);
                }
            }
        }

    }
}    