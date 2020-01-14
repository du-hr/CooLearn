<template>
  <div id="dashboard">
    <!-- Navigation Bar -->
    <nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0">
      <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="#">CooLearn</a>
      <input
        v-model="course_name"
        v-on:keyup.enter="seeOtherTutorsPricing(course_name, $event)"
        class="form-control form-control-dark w-100"
        type="text"
        placeholder="Search"
        aria-label="Search"
      />
      <ul class="navbar-nav px-3">
        <li class="nav-item text-nowrap">
          <a v-on:click="logout()" href="#" class="nav-link">Sign out</a>
        </li>
      </ul>
    </nav>

    <div class="container-fluid">
      <div class="row">
        <!-- Column Navigation Bar -->
        <!-- FIX ICONS and links and colors -->
        <!-- Display some sort of information if no requests available -->
        <nav class="col-md-2 d-none d-md-block bg-light sidebar">
          <div class="sidebar-sticky">
            <ul class="nav flex-column">
              <li class="nav-item">
                <a
                  class="nav-link active selection"
                  href="#"
                  v-on:click="setActiveTab('dashboard', $event)"
                >
                  <font-awesome-icon icon="home" />Dashboard
                </a>
              </li>
              <li class="nav-item">
                <a class="nav-link selection" href="#" v-on:click="setActiveTab('course', $event)">
                  <font-awesome-icon icon="book" />Courses
                </a>
              </li>
              <li class="nav-item">
                <a
                  class="nav-link selection"
                  href="#"
                  v-on:click="setActiveTab('availability', $event)"
                >
                  <font-awesome-icon icon="plus-square" />Availability
                </a>
              </li>
              <li class="nav-item">
                <a
                  class="nav-link selection"
                  href="#"
                  v-on:click="setActiveTab('review', $event)"
                >
                  <font-awesome-icon icon="star" />Reviews
                </a>
              </li>
              <li class="nav-item">
                <a
                  class="nav-link selection"
                  href="#"
                  v-on:click="setActiveTab('specificCourses', $event)"
                >
                  <font-awesome-icon icon="search" />Search courses
                </a>
              </li>
            </ul>
          </div>
        </nav>

        <!-- List of Requests -->
        <main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
          <div
            class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom"
          >
            <h1 class="h2">Dashboard</h1>
            <div class="btn-toolbar mb-2 mb-md-0">
              <p class="m-0">Logged in as {{userName}}</p>
            </div>
          </div>
          <!-- Might refactor to a component by itself -->
          <template v-if="activeTab === 'dashboard'">
            <div v-for="request in requests" v-bind:key="request.id" class="card mb-2">
              <table>
                <tr>
                  <th class="request-status">Status</th>
                  <th class="request-date">Date</th>
                  <th class="request-time">Start Time</th>
                  <th class="request-time">End Time</th>
                  <th>Course</th>
                  <th rowspan="2" class="request-button">
                    <button class="btn btn-sm btn-link" v-on:click="goToView(request)">View</button>
                  </th>
                  <th rowspan="2" class="request-button">
                    <button
                      class="btn btn-sm btn-success"
                      v-on:click="acceptSession(request.id)"
                    >Accept</button>
                  </th>
                  <th rowspan="2" class="request-button">
                    <button
                      class="btn btn-sm btn-danger"
                      v-on:click="rejectSession(request.id)"
                    >Reject</button>
                  </th>
                </tr>
                <tr>
                  <td class="request-status">{{request.status}}</td>
                  <td class="request-date">{{request.date}}</td>
                  <td class="request-time">{{request.startTime}}</td>
                  <td class="request-time">{{request.endTime}}</td>
                  <td class="text-capitalize">{{request.courseName}}</td>
                </tr>
              </table>
            </div>
          </template>
          <!-- 100 Depends on which link is the active one -->
          <template v-if="activeTab === 'course'">
            <div v-for="course in courses" v-bind:key="course.id" class="card mb-2">
              <table class="course-card">
                <tr>
                  <th class="course-name">Course</th>
                  <th class="course-price">Price</th>
                  <!-- 101 This should be true in begining (until a user presses on button) which will make it false -->
                  <template v-if="!isEdit[course.id]">
                    <th rowspan="2">
                      <button v-on:click="toggleEdit(course.id)" class="btn btn-sm btn-warning">Edit</button>
                    </th>
                  </template>
                  <template v-else>
                    <th rowspan="2">
                      <button
                        v-on:click="updatePrice(course.id)"
                        class="btn btn-sm btn-success"
                      >Save</button>
                    </th>
                  </template>
                </tr>
                <tr>
                  <td class="course-name">{{course.course}}</td>
                  <!-- Depends on value in 101 -->
                  <template v-if="!isEdit[course.id]">
                    <td class="course-price">${{course.hourlyRate}}/hour</td>
                  </template>
                  <template v-else>
                    <td class="course-price">
                      <input
                        v-model.number="hourlyRate"
                        type="number"
                        name="price"
                        min="12.5"
                        step="0.5"
                        id="price"
                        placeholder="$/hour"
                      />
                    </td>
                  </template>
                </tr>
              </table>
            </div>
          </template>
          <template v-if="activeTab === 'availability'">
            <div v-for="avail in availabilities" v-bind:key="avail.id" class="card mb-2">
              <table class="avail-card">
                <tr>
                  <th class="avail-id">ID</th>
                  <th class="avail-st">Start Time</th>
                  <th class="avail-et">End Time</th>
                  <th class="avail-dow">Day of the Week</th>
                  <th class="avail-sd">Start Date</th>
                  <th class="avail-ed">End Date</th>
                  <th rowspan="2">
                    <button v-on:click="edit(avail.id)" class="btn btn-sm btn-warning">Edit</button>
                  </th>
                  <th rowspan="2">
                    <button v-on:click="deleteAvail(avail.id)" class="btn btn-sm btn-danger">Delete</button>
                  </th>
                  <!-- 101 This should be true in begining (until a user presses on button) which will make it false -->
                </tr>
                <tr>
                  <td class="avail-id">{{avail.id}}</td>
                  <td class="avail-st">{{avail.startTime}}</td>
                  <td class="avail-et">{{avail.endTime}}</td>
                  <td class="avail-dow">{{avail.dayOfWeek}}</td>
                  <td class="avail-sd">{{avail.startDate}}</td>
                  <td class="avail-ed">{{avail.endDate}}</td>
                </tr>
              </table>
            </div>

            <br />
            <br />

            <div>
              <div
                class="d-flex justify-content-between flex-wrap flex-md-nowrap pb-2 mb-3 border-bottom"
              >
                <template v-if="idEdited<0">
                  <h3 class="h3">Add New Availability</h3>
                </template>
                <template v-else>
                  <h3 class="h3">Edit Availability with ID {{idEdited}}</h3>
                </template>
              </div>
              <div class="container">
                <div class="row">
                  <div class="col">
                    <div class="row justify-content-center">
                      <label for="startDate">Start Date</label>
                    </div>
                    <div class="row justify-content-center">
                      <input
                        type="date"
                        id="startDate"
                        :max="input.endDate"
                        v-model="input.startDate"
                      />
                    </div>
                  </div>
                  <div class="col">
                    <div class="row justify-content-center">
                      <label for="endDate">End Date</label>
                    </div>
                    <div class="row justify-content-center">
                      <input
                        type="date"
                        id="endDate"
                        :min="input.startDate"
                        v-model="input.endDate"
                      />
                    </div>
                  </div>
                  <div class="col">
                    <div class="row justify-content-center">
                      <label for="startTime">Start Time</label>
                    </div>
                    <div class="row justify-content-center">
                      <input
                        type="time"
                        id="startTime"
                        :max="input.endTime"
                        v-model="input.startTime"
                      />
                    </div>
                  </div>
                  <div class="col">
                    <div class="row justify-content-center">
                      <label for="endTime">End Time</label>
                    </div>
                    <div class="row justify-content-center">
                      <input
                        type="time"
                        id="endTime"
                        :min="input.startTime"
                        v-model="input.endTime"
                      />
                    </div>
                  </div>
                  <div class="col">
                    <div class="row justify-content-center">
                      <label>Day of the Week</label>
                    </div>
                    <div class="row justify-content-center">
                      <select v-model="input.dayOfWeek" name="dayOfWeek">
                        <option>MONDAY</option>
                        <option>TUESDAY</option>
                        <option>WEDNESDAY</option>
                        <option>THURSDAY</option>
                        <option>SATURDAY</option>
                        <option>SUNDAY</option>
                      </select>
                    </div>
                  </div>
                </div>

                <template v-if="idEdited<1">
                  <div class="row mt-4 justify-content-end">
                    <button v-on:click="saveChanges()" class="btn btn-success">Create</button>
                  </div>
                </template>
                <template v-else>
                  <div class="row mt-4 justify-content-between">
                    <button v-on:click="idEdited=-1;" class="btn btn-link">Back</button>
                    <button v-on:click="saveChanges()" class="btn btn-success">Save</button>
                  </div>
                </template>
              </div>
            </div>
          </template>
          <template v-if="activeTab === 'review'">
            <div v-for="request in completedReq" v-bind:key="request.id" class="card mb-2">
              <table v-for="(name,index) in request.studentNames" v-bind:key="index">
                <tr :key="reviewForceUpdate">
                  <th>Date</th>
                  <th>Student</th>
                  <th>Course</th>
                  <template v-if="getReviewOfStudent(request.id, request.studentIds[index]).comment==null">
                    <th rowspan="2" colspan="2">
                      <button class="btn btn-primary" v-on:click="goToWriteReview(request, index)">Review</button>
                    </th>
                  </template>
                  <template v-else>
                    <th rowspan="2">
                      <button class="btn btn-primary" v-on:click="goToEditReview(request, index, getReviewOfStudent(request.id, request.studentIds[index]))"> Edit</button>
                    </th>
                    <th rowspan="2">
                      <button class="btn btn-danger" v-on:click="deleteReview(request.id, request.studentIds[index], getReviewOfStudent(request.id, request.studentIds[index]).id)"> Delete </button>
                    </th>
                  </template>
                </tr>
                <tr>
                  <td>{{request.date}}</td>
                  <td>{{name}}</td>
                  <td>{{request.courseName}}</td>
                </tr>
              </table>
            </div>
          </template>
          <template v-if="activeTab === 'specificCourses'">
            <div
              v-for="specificCourse in specificCourses"
              v-bind:key="specificCourse.id"
              class="card mb-2"
            >
              <table>
                <tr>
                  <th class="specific-course">Course</th>
                  <th class="specific-tutor">Tutor</th>
                  <th class="specific-price">Price</th>
                </tr>
                <tr>
                  <td class="specific-course">{{specificCourse.course}}</td>
                  <td class="specific-tutor">{{specificCourse.tutorName}}</td>
                  <td class="specific-price">${{specificCourse.hourlyRate}}/hr</td>
                </tr>
              </table>
            </div>
          </template>
          <!-- Up might become a component by itself -->
        </main>
      </div>
    </div>
  </div>
</template>

<style scoped>
body {
  font-size: 0.875rem;
}

.feather {
  width: 16px;
  height: 16px;
  vertical-align: text-bottom;
}

/*
 * Sidebar
 */

.sidebar {
  position: fixed;
  top: 0;
  bottom: 0;
  left: 0;
  z-index: 100; /* Behind the navbar */
  padding: 0;
  box-shadow: inset -1px 0 0 rgba(0, 0, 0, 0.1);
}

.sidebar-sticky {
  position: -webkit-sticky;
  position: sticky;
  top: 48px; /* Height of navbar */
  height: calc(100vh - 48px);
  padding-top: 0.5rem;
  overflow-x: hidden;
  overflow-y: auto; /* Scrollable contents if viewport is shorter than content. */
}

.sidebar .nav-link {
  font-weight: 500;
  color: #333;
}

.sidebar .nav-link .feather {
  margin-right: 4px;
  color: #999;
}

.sidebar .nav-link.active {
  color: #007bff;
}

.sidebar .nav-link:hover .feather,
.sidebar .nav-link.active .feather {
  color: inherit;
}

.sidebar-heading {
  font-size: 0.75rem;
  text-transform: uppercase;
}

/*
 * Navbar
 */

.navbar-brand {
  padding-top: 0.75rem;
  padding-bottom: 0.75rem;
  font-size: 1rem;
  background-color: rgba(0, 0, 0, 0.25);
  box-shadow: inset -1px 0 0 rgba(0, 0, 0, 0.25);
}

.navbar .form-control {
  padding: 0.75rem 1rem;
  border-width: 0;
  border-radius: 0;
}

.form-control-dark {
  color: #fff;
  background-color: rgba(255, 255, 255, 0.1);
  border-color: rgba(255, 255, 255, 0.1);
}

.form-control-dark:focus {
  border-color: transparent;
  box-shadow: 0 0 0 3px rgba(255, 255, 255, 0.25);
}

/*
 * Utilities
 */

.border-top {
  border-top: 1px solid #e5e5e5;
}
.border-bottom {
  border-bottom: 1px solid #e5e5e5;
}

/*

 * Courses and Requests
 */

.course-card .course-name {
  width: 50%;
}

.course-card .course-price {
  width: 30%;
}

.request-date,
.request-time,
.request-status,
.request-button {
  width: 10%;
}

.specific-price {
  width: 20%;
}

.specific-tutor {
  width: 30%;
}

/*
 * Input
 */

input[type="number"] {
  text-align: center;
}

.avail-card .avail-id {
  width: 5%;
}

.avail-card .avail-st {
  width: 15%;
}

.avail-card .avail-et {
  width: 15%;
}
.avail-card .avail-dow {
  width: 15%;
}
.avail-card .avail-sd {
  width: 15%;
}
.avail-card .avail-ed {
  width: 15%;
}
</style>

<script src="./dashboard.js">
</script>
