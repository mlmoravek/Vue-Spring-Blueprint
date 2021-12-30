<template>
  <header>
    <b-navbar type="is-primary">
      <template #brand>
        <b-navbar-item
          tag="router-link"
          :to="{ path: '/' }"
          aria-label="Home Page"
        >
          <img src="@/assets/images/logo_white.png" alt="Init Logo" />
        </b-navbar-item>
      </template>
      <template #end>
        <template v-if="isLoggedIn">
          <b-navbar-item
            tag="router-link"
            :to="{ name: 'Accounts' }"
            aria-label="Accounts Page"
          >
            Accounts
          </b-navbar-item>
          <b-navbar-item
            tag="router-link"
            :to="{ name: 'Profile' }"
            aria-label="Profile Page"
          >
            Profile
          </b-navbar-item>

          <b-navbar-item @click="logout()" aria-label="Logout Page">
            Logout
          </b-navbar-item>
        </template>
        <template v-else>
          <b-navbar-item
            @click="isLoginDialogOpen = true"
            aria-label="Login Page"
          >
            Login
          </b-navbar-item>
          <b-navbar-item
            tag="router-link"
            :to="{ name: 'Register' }"
            aria-label="Registrieren Page"
          >
            Registrieren
          </b-navbar-item>
        </template>
      </template>
    </b-navbar>
    <LoginDialog v-model="isLoginDialogOpen" />
  </header>
</template>

<script lang="ts">
import Vue from "vue";
import LoginDialog from "@/components/dialog/LoginDialog.vue";
import { authServiceInstance as authService } from "@/services/AuthService";
import { mapGetters } from "vuex";
import { goToHome } from "@/router";
import AlertService from "@/services/libs/AlertService";

export default Vue.extend({
  name: "Header",
  components: {
    LoginDialog,
  },
  data: function () {
    return {
      isLoginDialogOpen: false,
    };
  },
  computed: {
    ...mapGetters({
      isLoggedIn: "isLoggedIn",
    }),
  },
  methods: {
    logout() {
      authService.logout();
      this.$store.commit("clearUser");
      // show logout message
      AlertService.info("Ausgeloggt");
      goToHome();
    },
  },
});
</script>

<style lang="scss" scoped></style>
