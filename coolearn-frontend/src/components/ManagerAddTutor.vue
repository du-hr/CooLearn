<template>
  <div class="addTutor">
    <div class="container">
      <div class="row mt-4">
        <img
          class="mb-4"
          src="https://www.emoji.co.uk/files/twitter-emojis/symbols-twitter/11124-squared-cl.png"
          alt
          width="72"
          height="72"
        />
        <h1 class="display-4 ml-4">Coolearn Registration Form</h1>
      </div>
      <form>
        <div class="row">
          <div class="form-group">
            <label for="firstName">First Name</label>
            <input type="text" class="form-control" id="firstName" placeholder="John" v-model="firstName"/>
          </div>
        </div>

        <div class="row">
          <div class="form-group">
            <label for="lastName">Last Name</label>
            <input type="text" class="form-control" id="lastName" placeholder="Doe" v-model="lastName" />
          </div>
        </div>
        <div class="row">
          <div class="form-group">
            <label for="emailAddress">Email address</label>
            <input
              type="email"
              class="form-control"
              id="emailAddress"
              placeholder="name@example.com"
              v-model="emailAddress"
            />
          </div>
        </div>
        <div class="row">
          <div class="form-group" id="interact">
            <label for="courses">List of courses</label>
            <select
              v-model="listOfCourses" multiple
              class="form-control"
              id="courses">
              <option v-for="course in courseOptions" v-bind:value="course.name" v-bind:key="course.name">{{course.name}}</option>
            </select>
          </div>
        </div>
         <div class="row mt-3">
          <p v-if="errorMessage" style="color:red" class="text-center">{{errorMessage}}</p>
        </div>
        <div class="row d-flex justify-content-end my-3">
          <button class="btn btn-success" v-on:click="registerTutor">Submit</button>
        </div>
      </form>
    </div>
  </div>
</template>

<style scoped>
/* Add background color */

.row {
  justify-content: center;
  width: 100%;
  margin: 0;
}

.form-group {
  width: 30%;
}

input {
  text-align: center;
}

div[class="justify-content-end"] {
  width: 80%;
}
</style>

<script src="./addTutor.js">
</script>
