<template>
  <div id="app" class="app-shell">
    <header class="topbar">
      <router-link class="brand" to="/">网上信息发布</router-link>

      <nav class="subsystem-links">
        <a href="https://trade.example.com" target="_blank" rel="noopener noreferrer">交易系统</a>
        <a href="https://account.example.com" target="_blank" rel="noopener noreferrer">账户系统</a>
      </nav>

      <div class="actions">
        <span v-if="userStore.token" class="role-pill">{{ roleLabel }}</span>
        <button v-if="!userStore.token" class="ghost-btn" type="button" @click="handleLogin">登录</button>
        <button v-if="!userStore.token" class="primary-btn" type="button" @click="handleLogin">注册</button>
        <button v-else class="ghost-btn" type="button" @click="handleLogout">退出</button>
      </div>
    </header>

    <main class="page-shell">
      <router-view />
    </main>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRouter } from 'vue-router'
import { useUserStore } from './stores/user'

const router = useRouter()
const userStore = useUserStore()

const roleLabel = computed(() => {
  if (userStore.isPremiumVip) return 'PREMIUM_VIP'
  if (userStore.isStandard) return 'STANDARD'
  return 'GUEST'
})

const handleLogin = () => {
  const target = encodeURIComponent(window.location.href)
  window.location.href = `https://account.example.com/login?redirect=${target}`
}

const handleLogout = () => {
  userStore.logout()
  router.push('/')
}
</script>

<style scoped>
.app-shell {
  min-height: 100vh;
  background: linear-gradient(180deg, #07111f 0%, #0f172a 100%);
  color: #eef2ff;
}

.topbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 16px;
  padding: 14px 20px;
  border-bottom: 1px solid rgba(148, 163, 184, 0.18);
  background: rgba(15, 23, 42, 0.92);
  position: sticky;
  top: 0;
  z-index: 10;
}

.brand {
  font-size: 1.1rem;
  font-weight: 700;
  color: #fff;
  text-decoration: none;
  letter-spacing: 0.08em;
  text-transform: uppercase;
}

.subsystem-links {
  display: flex;
  gap: 12px;
}

.subsystem-links a {
  color: #dbeafe;
  text-decoration: none;
  font-size: 0.95rem;
}

.actions {
  display: flex;
  align-items: center;
  gap: 10px;
}

.role-pill,
.primary-btn,
.ghost-btn {
  border-radius: 999px;
  padding: 8px 12px;
  border: 1px solid transparent;
  font-size: 0.92rem;
  cursor: pointer;
}

.role-pill {
  background: rgba(129, 140, 248, 0.18);
  border-color: rgba(129, 140, 248, 0.35);
  color: #e0e7ff;
}

.primary-btn {
  background: linear-gradient(135deg, #818cf8, #38bdf8);
  color: #08111f;
  font-weight: 700;
}

.ghost-btn {
  background: rgba(15, 23, 42, 0.7);
  color: #e2e8f0;
  border-color: rgba(148, 163, 184, 0.4);
}

.page-shell {
  padding: 24px;
}
</style>
