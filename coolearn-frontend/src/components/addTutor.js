import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = 'http://' + config.dev.backendHost + ':' + config.dev.backendPort

var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    name: 'addTutor',
    data() {
        return {
            firstName: "",
            lastName: "",
            emailAddress: "",
            listOfCourses: [],
            courseOptions: [],
            currentCourse: "",
            errorMessage: ""
        }
    },
    created: function () {
      AXIOS.get('/courses')
      .then(response => {
        this.courseOptions = response.data
      })
    },
    methods: {
        registerTutor: function () {
            //Interactive courses
            if (this.currentCourse !== "") {
                this.listOfCourses.push(this.currentCourse);
            }

            AXIOS.post(`/register`, {
                firstName: this.firstName,
                lastName: this.lastName,
                emailAddress: this.emailAddress,
                listOfCoursesName: this.listOfCourses
            })
            .then(response => {
                  //reload
                  alert('Tutor Created! They will receive an email shortly.')
                  this.clearInput();
            })
            .catch(e => {
                  this.errorMessage = e.response.data.message;
            });
        },
        clearInput: function () {
            console.log(this); //KEEP this (correct way is to use apply, bind or call I need to read more about them)
            this.firstName = '';
            this.lastName = '';
            this.emailAddress = '';
            this.currentCourse = '';
            this.errorMessage = '';
            var addedItems = document.querySelectorAll(".new");
            addedItems.forEach((e) => {
                e.remove();
            })
        }
    }
}
