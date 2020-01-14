<template>
  <div id="availability">
    <nav class="navbar navbar-dark sticky-top bg-dark flex-md-nowrap p-0">
      <!--      click the icon and redirected to dashboard-->
      <a class="navbar-brand col-sm-3 col-md-2 mr-0" href="#/dashboard">CooLearn</a>
      <ul class="navbar-nav px-3">
        <li class="nav-item text-nowrap">
          <a v-on:click="logout()" href="#" class="nav-link">Sign out</a>
        </li>
      </ul>
    </nav>
    <main role="main" class="col-md-9 ml-sm-auto col-lg-10 pt-3 px-4">
      <div
        class="d-flex justify-content-between flex-wrap flex-md-nowrap align-items-center pb-2 mb-3 border-bottom"
      >
        <h1 class="h2">Availabilities</h1>
      </div>

      <!--      Table to display availability attributes-->
      <div v-for="avail in availabilities" v-bind:key="avail.id" class="card">
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
        <table>
          <tr>
            <th>Start Date</th>
            <th>End Date</th>
          </tr>
          <tr>
            <td>
              <input type="date" :max="input.endDate" v-model="input.startDate" />
            </td>
            <td>
              <input type="date" :min="input.startDate" v-model="input.endDate" />
            </td>
          </tr>
          <tr>
            <th>Start Time</th>
            <th>End Time</th>
          </tr>
          <tr>
            <td>
              <input type="time" :max="input.endTime" v-model="input.startTime" />
            </td>
            <td>
              <input type="time" :min="input.startTime" v-model="input.endTime" />
            </td>
          </tr>
          <template v-if="idEdited<1">
            <tr>
              <th>Day of the Week</th>
              <th rowspan="2">
                <button v-on:click="saveChanges()" class="btn btn-lg btn-success">Create</button>
              </th>
            </tr>
            <td>
              <select v-model="input.dayOfWeek" name="dayOfWeek">
                <option>MONDAY</option>
                <option>TUESDAY</option>
                <option>WEDNESDAY</option>
                <option>THURSDAY</option>
                <option>SATURDAY</option>
                <option>SUNDAY</option>
              </select>
            </td>
          </template>
          <template v-else>
            <tr>
              <th colspan="2">Day of the Week</th>
            </tr>
            <tr>
              <td colspan="2">
                <select v-model="input.dayOfWeek" name="dayOfWeek">
                  <option>MONDAY</option>
                  <option>TUESDAY</option>
                  <option>WEDNESDAY</option>
                  <option>THURSDAY</option>
                  <option>SATURDAY</option>
                  <option>SUNDAY</option>
                </select>
              </td>
            </tr>
            <tr>
              <td>
                <button
                  title="Back to Add Availability Menu"
                  v-on:click="idEdited=-1"
                  class="btn btn-lg btn-primary"
                >Back</button>
              </td>
              <td>
                <button v-on:click="saveChanges()" class="btn btn-lg btn-success">Save</button>
              </td>
            </tr>
          </template>
        </table>
        <br />
        <a href="#/dashboard" class="btn btn-lg btn-secondary">Return to Dashboard</a>
      </div>
      <br />
      <br />
    </main>
  </div>
</template>


<script src="./availability.js">
</script>

<style scoped>
/*
   * Table
   */

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
