<script setup lang="ts">
import { useUserStore } from '@/stores/user'
import { watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const userStore = useUserStore()
const route = useRoute()
const router = useRouter()

watch(() => userStore.isLoggedIn, (val) => {
  if (val) userStore.fetchMe()
}, { immediate: true })
</script>

<template>
  <div id="app-container">
    <router-view />
  </div>
</template>

<style>
* { margin: 0; padding: 0; box-sizing: border-box; }
html, body, #app, #app-container { height: 100%; font-family: 'Helvetica Neue', sans-serif; }
</style>