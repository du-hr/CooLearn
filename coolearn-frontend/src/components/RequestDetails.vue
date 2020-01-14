<template>
  <div>
    <div class="container">
      <div class="row mt-3 justify-content-start">
        <button class="btn btn-sm btn-link" v-on:click="goBack()">Go Back</button>
      </div>
      <div class="row">
        <div class="col-6 my-auto">
          <div class="row justify-content-center">
            <h2 class="my-2">Details</h2>
          </div>
          <ul class="list-group list-group-flush">
            <li class="list-group-item">Session Status: {{this.req.status}}</li>
            <li class="list-group-item">Course: {{this.req.courseName}}</li>
            <li class="list-group-item">Date: {{this.req.date}}</li>
            <li class="list-group-item">Time: from {{this.req.startTime}} to {{this.req.endTime}}</li>
            <li
              v-for="(name, index) in this.req.studentNames"
              v-bind:key="index"
              class="list-group-item"
            >Student: {{name}}</li>
            <div v-for="(comment, index) in comments" v-bind:key="comment.id" class="card group">
              <div class="card-header px-3 d-flex justify-content-between">
                <span v-if="sessions_list[index] !== undefined">{{sessions_list[index].date}}</span>
                <span v-if="sessions_list[index] !== undefined">{{sessions_list[index].courseName}}</span>
                <!-- CONVERT TO FLOAT -->
                <span>
                  {{comment.rating}}/5.0
                  <span id="stars"><font-awesome-icon icon="star" /></span>
                </span>
              </div>
              <div class="card-body">
                <blockquote class="blockquote mb-0 my-3">
                  <p>{{comment.comment}}</p>
                </blockquote>
              </div>
            </div>
            <li class="list-group-item" v-if="this.req.roomId > 0">Room Number: {{this.req.roomId}}</li>
          </ul>
        </div>
        <div class="col-6 my-auto">
          <!-- <img src="./imgs/book_imgs/book1.jpg" alt class="slideshow" /> -->
          <!-- <img src="./imgs/book_imgs/book2.jpg" alt class="slideshow" /> -->
          <!-- <img src="./imgs/book_imgs/book3.jpg" alt class="slideshow" /> -->
          <img src="./imgs/book_imgs/book4.jpg" alt class="slideshow" />
          <!-- <img src="./imgs/book_imgs/book5.jpg" alt class="slideshow" /> -->
        </div>
      </div>
    </div>

    <br />
    <template v-if="this.comments.size > 0">
      <h2 class="my-2">Reviews</h2>
      <div v-for="(comment,index) in comments" v-bind:key="index" class="card">
        <div class="card-header">{{comment.rating}} Stars</div>
        <div class="card-body">
          <blockquote class="blockquote mb-0">
            <p>{{comment.comment}}</p>
          </blockquote>
        </div>
      </div>
    </template>
  </div>
</template>

<style scoped>
img {
  max-width: 100%;
  max-height: 500px;
  object-fit: cover;
}

.group {
  width: 80%;
  margin: 0.5rem auto;
}

.group div {
  padding: 0;
}
blockquote p {
  font-size: 0.8em;
  margin: 0;
}
</style>

<script src="./requestDetails.js">
</script>