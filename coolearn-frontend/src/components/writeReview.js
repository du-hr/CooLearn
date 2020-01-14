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
    name: 'writeReview',
    data() {
        return {
            req:{},
            student:{
                name:"",
                id: Number
            },
            rating: Number
        }
    },

    created:function(){
        let userRole_id = this.getUserRoleId();
        this.req=JSON.parse(localStorage.getItem('requestToReview'));
        this.student.name=localStorage.getItem('studentName');
        this.student.id=parseInt(localStorage.getItem('studentId'));
        console.log(this.student.name);
        console.log(this.student.id);
    },

    methods: {
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

        postReview: function(){
            let userRole_id = this.getUserRoleId();
            let session_id = this.req.id;
            let reviewee_id = this.student.id;
            AXIOS.post(`/dashboard/${userRole_id}/${session_id}/${reviewee_id}`, {
                comments:document.getElementsByTagName('textarea')[0].value,
                rating:this.rating
              }).then(response => {
              }).catch(e => {
                alert(e.response.data.message)
              });
              this.goBack();
        },

        goBack: function(){
            localStorage.removeItem('requestToReview');
            localStorage.removeItem('studentName');
            localStorage.removeItem('studentId');
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