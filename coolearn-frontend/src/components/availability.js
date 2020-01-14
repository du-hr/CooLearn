import axios from 'axios'

var config = require('../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = 'http://' + config.dev.backendHost + ':' + config.dev.backendPort

var AXIOS = axios.create({
  baseURL: backendUrl,
  headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
  name: 'Availability',
  data() {
    return {
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
    logout: function () {
      localStorage.clear();
      this.$router.push('login')
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
