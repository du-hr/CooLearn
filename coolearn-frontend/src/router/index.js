import Vue from 'vue'
import Router from 'vue-router'

import Dashboard from '@/components/Dashboard.vue'
import Login from '@/components/Login.vue'
import Homepage from '@/components/Homepage.vue'
import ManagerForm from '@/components/ManagerAddTutor.vue'
import Availability from '@/components/Availability.vue'
import RequestDetails from '@/components/RequestDetails.vue'
import WriteReview from '@/components/WriteReview.vue'
import EditReview from '@/components/EditReview.vue'

Vue.use(Router)

export default new Router({
  routes: [
    {
      path: '/',
      name: 'Homepage',
      component: Homepage
    },
    {
      path: '/dashboard',
      name: 'Dashboard',
      component: Dashboard
    },
    {
      path: '/login',
      name: 'Login',
      component: Login
    },
    {
      path: '/secret',
      name: 'ManagerForm',
      component: ManagerForm
    },
    {
      path:'/availability',
      name: 'Availability',
      component: Availability
    },
    {
      path:'/dashboard/:userRole_id/:request_id',
      name:'Request Details',
      component: RequestDetails
    },
    {
      path:'/review/create',
      name:'ReviewWriting',
      component: WriteReview
    },

    {
      path:'/review/edit',
      name:'ReviewEditing',
      component: EditReview
    }
  ]
})
