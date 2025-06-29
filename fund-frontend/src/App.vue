<template>
  <div>
    <h1>Fund Data</h1>
    <button @click="loadFunds">Load Funds</button>
    <pre>{{ JSON.stringify(funds, null, 2) }}</pre>
  </div>
</template>

<script setup>
import { ref } from 'vue'

const funds = ref([])

async function loadFunds() {
  try {
    const res = await fetch('http://localhost:8080/api/funds')
    funds.value = await res.json()
  } catch (error) {
    console.error('Failed to load funds:', error)
  }
}
</script>
