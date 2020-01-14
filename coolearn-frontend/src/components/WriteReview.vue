<template>
  <div class="form-group">
    <label for="exampleFormControlTextarea1">Review</label>
    <!--- TODO: something to choose student --->
    <textarea
      class="form-control"
      id="exampleFormControlTextarea1"
      placeholder="Write review"
      rows="7"
    ></textarea>
    <!--- TODO: rating --->
    <!-- <div class="rate">
      <input type="radio" id="star5" name="rate" value="5">
      <label for="star5" title="text">5 stars</label>
      <input type="radio" id="star4" name="rate" value="4">
      <label for="star4" title="text">4 stars</label>
      <input type="radio" id="star3" name="rate" value="3">
      <label for="star3" title="text">3 stars</label>
      <input type="radio" id="star2" name="rate" value="2">
      <label for="star2" title="text">2 stars</label>
      <input type="radio" id="star1" name="rate" value="1">
      <label for="star1" title="text">1 star</label>
    </div>-->
    Rating: <input
      v-model.number="rating"
      type="number"
      name="rate"
      min="0"
      max="5"
      step="0.5"
      id="price"
      placeholder="rating"
    >
    <br>

    <button class="btn btn-primary" v-on:click="postReview()">Submit</button>
  </div>
</template>
<!---
<style scoped>
.rate {
  float: left;
  height: 46px;
  padding: 0 10px;
}
.rate:not(:checked) > input {
  position: absolute;
  top: -9999px;
}
.rate:not(:checked) > label {
  float: right;
  width: 1em;
  overflow: hidden;
  white-space: nowrap;
  cursor: pointer;
  font-size: 30px;
  color: #ccc;
}
.rate:not(:checked) > label:before {
  content: "★ ";
}
.rate > input:checked ~ label {
  color: #ffc700;
}
.rate:not(:checked) > label:hover,
.rate:not(:checked) > label:hover ~ label {
  color: #deb217;
}
.rate > input:checked + label:hover,
.rate > input:checked + label:hover ~ label,
.rate > input:checked ~ label:hover,
.rate > input:checked ~ label:hover ~ label,
.rate > label:hover ~ input:checked ~ label {
  color: #c59b08;
}
</style>
--->



<script src="./writeReview.js">
</script>



    


