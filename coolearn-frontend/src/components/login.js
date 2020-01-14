import axios from 'axios'
var config = require('../../config')

var frontendUrl = 'http://' + config.dev.host + ':' + config.dev.port
var backendUrl = 'http://' + config.dev.backendHost + ':' + config.dev.backendPort

var AXIOS = axios.create({
    baseURL: backendUrl,
    headers: { 'Access-Control-Allow-Origin': frontendUrl }
})

export default {
    name: 'Login',
    data() {
        return {
            input: {
                username: "",
                password: "",
                picked: "Tutor"
            }
        }
    },
    methods: {
        login: function() {
            //Check Validity of username/password
            AXIOS.post("/login/", {

                emailAddress: this.input.email,
                password: this.input.password,
                type: this.input.picked
            }).then(response => {
                if (this.input.picked == "Student") {
                    alert("The Student Page is Under Development")
                    return
                }
                console.log(response)
                localStorage.setItem('userRoleId',response.data.id)
                this.$router.push('dashboard')
            }).catch(e => {
                alert(e.response.data.message)
            })
        }
    }
}
