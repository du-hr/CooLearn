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
    name: 'dashboard',
    data() {
        return {
            activeTab: 'dashboard',
            isEdit: [],
            requests: [],
            courses: [],
            hourlyRate: undefined,
            completedReq:[],
            review:{},
            reviewForceUpdate: 0,
            specificCourses: [],
            tutors: [],
            course_name: '',
            userName: '',
            availabilities: [],
            idEdited: -1,
            input: {
                startTime: null,
                endTime: null,
                dayOfWeek: null,
                startDate: null,
                endDate: null
            }
        }
    },
    created: function () {
        // Initializing people from backend
        //Get userRoleid
        let userRole_id = this.getUserRoleId()
        this.getRequests();
        //Get list of courses
        AXIOS.get(`/dashboard/${userRole_id}/courses`)
            .then(response => {
                // JSON responses are automatically parsed.
                this.courses = response.data;
                this.courses.forEach((course) => {
                    this.isEdit[course.id] = false;
                });
            })
            .catch(e => {
                console.log(e);
            });
        this.getCompletedSessions()

        AXIOS.get(`/getName/` + userRole_id)
            .then(response => {
                // JSON responses are automatically parsed.
                this.userName = response.data;
            })
            .catch(e => {
                console.log(e);
            });

            AXIOS.get(`/dashboard/${userRole_id}/availabilities`)
      .then(response => {
        // JSON responses are automatically parsed.
        this.availabilities = response.data;
      })
      .catch(e => {
        console.log(e);
      });

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
        toggleEdit: function (id) {
            Vue.set(this.isEdit, id, !this.isEdit[id]);
        },
        setActiveTab: function (name, event) {
            this.activeTab = name;
            document.querySelectorAll(".selection").forEach((e) => {
                e.classList.remove("active");
            });
            event.srcElement.classList.add("active");
        },
        updatePrice: function (id) {
            let userRole_id = this.getUserRoleId();
            let course_id = id;
            AXIOS.put(`/dashboard/${userRole_id}/courses/${course_id}?hourlyRate=${this.hourlyRate}`)
                .then(response => {
                    var i;
                    for (i in this.courses) {
                        if (this.courses[i].id == course_id) {
                            this.courses[i].hourlyRate = this.hourlyRate
                        }
                    }
                    this.hourlyRate = ''
                })
                .catch(e => {
                    var errorMsg = e.message;
                    console.log("ERROR")
                    console.log(errorMsg)
                });
            this.toggleEdit(id);
        },
        logout: function () {
            localStorage.clear();
            this.$router.push('login')
        },
        acceptSession: function (id) {
            let userRole_id = this.getUserRoleId();
            let request_id = id;
            AXIOS.put(`/dashboard/${userRole_id}/${request_id}/accept`)
                .then(response => {
                    //reload
                    this.getRequests();
                })
                .catch(e => {
                    var errorMsg = e.message;
                });
            this.toggleEdit();
        },
        rejectSession: function (id) {
            let userRole_id = this.getUserRoleId();
            let request_id = id;
            AXIOS.put(`/dashboard/${userRole_id}/${request_id}/decline`)
                .then(response => {
                    //reload
                    this.getRequests();
                })
                .catch(e => {
                    var errorMsg = e.message;
                });
        },
        getRequests: function () {
            let userRole_id = this.getUserRoleId();
            AXIOS.get(`/dashboard/` + userRole_id)
                .then(response => {
                    // JSON responses are automatically parsed.
                    this.requests = response.data;
                })
                .catch(e => {
                    console.log(e);
                });
        },
        getCompletedSessions: function(){
            let userRole_id = this.getUserRoleId();
            AXIOS.get(`/request/` + userRole_id)
                .then(response => {
                    // JSON responses are automatically parsed.
                    this.completedReq = response.data;
                    console.log(this.completedReq[0]);
                })
                .catch(e => {
                    console.log(e.response.data.message);
                });

        },
        goToView: function(session){
            let userRole_id = this.getUserRoleId();
            let id = session.id;
            localStorage.setItem('current_request',JSON.stringify(session));
            console.log(this.requests);
            this.$router.push({path:`dashboard/${userRole_id}/${id}`});
        },

        goToWriteReview: function(session,index){
            let userRole_id = this.getUserRoleId();
            localStorage.setItem('requestToReview',JSON.stringify(session));
            localStorage.setItem('studentName',session.studentNames[index]);
            localStorage.setItem('studentId',session.studentIds[index]);
            console.log(this.requests);
            this.$router.replace({path:`/review/create`});
        },
        getReviewOfStudent: function(req_id, stud_id){
            let reviewer_id = this.getUserRoleId();
            let session_id = req_id;
            let reviewee_id = stud_id;
            AXIOS.get(`/dashboard/${reviewer_id}/${session_id}/${reviewee_id}`)
            .then(response => {
                // JSON responses are automatically parsed.
                this.review = response.data;
            })
            .catch(e => {
                this.review.comment = null
                console.log(e.response.data);
            });
            return this.review;
        },

        goToEditReview: function(session,index, review){
            let userRole_id = this.getUserRoleId();
            localStorage.setItem('requestToReview',JSON.stringify(session));
            localStorage.setItem('studentName',session.studentNames[index]);
            localStorage.setItem('studentId',session.studentIds[index]);
            localStorage.setItem('reviewToEdit', JSON.stringify(review));
            console.log(this.requests);
            this.$router.replace({path:`/review/edit`});
        },

        deleteReview: function(req_id, stud_id, rev_id){
            let reviewer_id = this.getUserRoleId();
            let session_id= req_id;
            let reviewee_id =stud_id;
            let review_id=rev_id;
            AXIOS.delete(`/dashboard/${reviewer_id}/${session_id}/${reviewee_id}/${review_id}`)
            .then(response => {
              console.log('successful deletion')
              this.reviewForceUpdate += 1
            })
            .catch(e => {
                alert(e.response.data.message)
            })
            this.getCompletedSessions();
        },
        seeOtherTutorsPricing: function (course_name, event) {
            let userRole_id = this.getUserRoleId();
            AXIOS.get(`/dashboard/${userRole_id}/${course_name}/specificCourses`)
                .then(response => {
                    this.specificCourses = response.data;
                })
                .catch(e => {
                    var errorMsg = e.message;
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
          // delete and auto refresh
          deleteAvail: function (id) {
            let userRole_id = this.getUserRoleId()
            let index = -1
            for (var i in this.availabilities) {
              if (this.availabilities[i].id === id) {
                index = i
                break
              }
            }
            if (index == -1) {
              console.log("ERROR! ID NOT FOUND!")
              return
            }
            let refresh = false
            AXIOS.delete(`/dashboard/${userRole_id}/availabilities/${id}`)
              .then(response => {
                this.availabilities.splice(index, 1)
                if (this.idEdited == id) {
                  this.idEdited = -1
                }
              })
              .catch(e => {
                console.log(e)
              })
          },
          edit: function (id) {
            this.idEdited = id
            let av = this.getAvFromId(id)
            if (av === null) {
              alert("The availability was not found?")
              this.idEdited = -1
              return
            }
            this.input.dayOfWeek = av.dayOfWeek
            this.input.startDate = av.startDate
            this.input.endDate = av.endDate
            this.input.startTime = av.startTime
            this.input.endTime = av.endTime
          },
          saveChanges: function () {
            let userRole_id = this.getUserRoleId()
            if (this.idEdited < 0) {
              //Add new availability
              AXIOS.post(`/dashboard/${userRole_id}/availabilities`, {
                startTime: this.input.startTime,
                endTime: this.input.endTime,
                startDate: this.input.startDate,
                endDate: this.input.endDate,
                dayOfWeek: this.input.dayOfWeek
              }).then(response => {
                this.availabilities.push(response.data)
              }).catch(e => {
                alert(e.response.data.message)
              })
            } else {
              //save cahnges to av
              AXIOS.put(`/dashboard/${userRole_id}/availabilities/${this.idEdited}`, {
                startTime: this.input.startTime,
                endTime: this.input.endTime,
                startDate: this.input.startDate,
                endDate: this.input.endDate,
                dayOfWeek: this.input.dayOfWeek
              }).then(response => {
                let i = this.getIndexFromId(this.idEdited)
                this.availabilities[i].startTime = this.input.startTime
                this.availabilities[i].endTime = this.input.endTime
                this.availabilities[i].startDate = this.input.startDate
                this.availabilities[i].endDate = this.input.endDate
                this.availabilities[i].dayOfWeek = this.input.dayOfWeek
              }).catch(e => {
                alert(e.response.data.message)
              })
            }
          },
          getIndexFromId: function (id) {
            var i
            for (i in this.availabilities) {
              if (this.availabilities[i].id == id) {
                return i
              }
            }
            return -1
          },
          getAvFromId: function (id) {
            for (var av of this.availabilities) {
              if (av.id == id) {
                return av
              }
            }
            return null
          }
    }
}
