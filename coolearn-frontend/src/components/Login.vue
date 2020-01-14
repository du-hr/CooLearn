<template>
  <div id="login">
    <div class="text-center">
      <form class="form-signin">
        <img
          class="mb-4"
          src="https://www.emoji.co.uk/files/twitter-emojis/symbols-twitter/11124-squared-cl.png"
          alt
          width="72"
          height="72"
        />
        <h1 class="h3 mb-3 font-weight-normal">Please sign in</h1>
        <label for="inputEmail" class="sr-only">Email address</label>
        <input
          type="email"
          id="inputEmail"
          class="form-control"
          placeholder="Email address"
          v-model="input.email"
          required
          autofocus
        />
        <label for="inputPassword" class="sr-only">Password</label>
        <input
          type="password"
          id="inputPassword"
          class="form-control"
          placeholder="Password"
          v-model="input.password"
          required
        />
        <span>I am a</span>
        <br />
        <input type="radio" name="role" id="tutor" value="Tutor" v-model="input.picked" />
        <label for="tutor">Tutor</label>
        <input type="radio" name="role" id="student" value="Student" v-model="input.picked" />
        <label for="student">Student</label>
        <button
          class="btn btn-lg btn-primary btn-block"
          type="button"
          v-on:click="login()"
        >Log in as a {{input.picked}}</button>
        <p class="mt-5 mb-3 text-muted">&copy; 2019-2020</p>
      </form>
    </div>
  </div>
</template>

<style scoped>
html,
body {
  height: 100%;
}

body {
  display: -ms-flexbox;
  display: -webkit-box;
  display: flex;
  -ms-flex-align: center;
  -ms-flex-pack: center;
  -webkit-box-align: center;
  align-items: center;
  -webkit-box-pack: center;
  justify-content: center;
  padding-top: 40px;
  padding-bottom: 40px;
  background-color: #f5f5f5;
}

.form-signin {
  width: 100%;
  max-width: 330px;
  padding: 15px;
  margin: 0 auto;
}
.form-signin .checkbox {
  font-weight: 400;
}
.form-signin .form-control {
  position: relative;
  box-sizing: border-box;
  height: auto;
  padding: 10px;
  font-size: 16px;
}
.form-signin .form-control:focus {
  z-index: 2;
}
.form-signin input[type="email"] {
  margin-bottom: -1px;
  border-bottom-right-radius: 0;
  border-bottom-left-radius: 0;
}
.form-signin input[type="password"] {
  margin-bottom: 10px;
  border-top-left-radius: 0;
  border-top-right-radius: 0;
}
</style>
<script src="./login.js">
</script>
