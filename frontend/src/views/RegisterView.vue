<template>
  <div class="container">
    <section class="section">
      <p class="title has-text-primary">
        Erstelle einen neuen Account für dieses super Portal :D
      </p>
      <img alt="Partyhead image" src="../assets/images/partyhead.png" />
    </section>
    <form class="section" @submit="onSave">
      <h1 class="title">Registrieren</h1>
      <b-notification
        v-show="error"
        type="is-danger"
        aria-close-label="Close notification"
        :message="error"
        @close="error = ''"
      />
      <b-field label="Username" expanded>
        <b-input v-model="user.username" required />
      </b-field>
      <b-field grouped>
        <b-field label="Vorname" expanded>
          <b-input v-model="user.firstName" required />
        </b-field>
        <b-field label="Nachname" expanded>
          <b-input v-model="user.lastName" required />
        </b-field>
      </b-field>
      <b-field label="Email" expanded>
        <b-input type="email" v-model="user.email" required />
      </b-field>

      <PasswordInput
        v-model="user.password"
        @input="(e) => (passwordValidationError = e)"
        @confirm="(p) => (user.confirmPassword = p)"
      />

      <b-field>
        <b-button
          label="Registrieren"
          aria-label="Registrieren Button"
          native-type="submit"
        />
      </b-field>
    </form>
    <LoginDialog v-model="isLoginDialogOpen" />

    <b-loading :is-full-page="true" v-model="isLoading" />
  </div>
</template>

<script lang="ts">
import Vue from "vue";
import LoginDialog from "@/components/dialog/LoginDialog.vue";
import { NewUser } from "@/types";
import { userServiceInstance as userService } from "@/services/UserService";
import AlertService from "@/services/libs/AlertService";
import PasswordInput from "@/components/controls/PasswordInput.vue";

export default Vue.extend({
  name: "RegisterView",
  components: { LoginDialog, PasswordInput },
  data: function () {
    return {
      user: { password: "" } as NewUser,
      error: "",
      passwordValidationError: "",
      isLoading: false,
      isLoginDialogOpen: false,
    };
  },
  methods: {
    onSave(e: Event) {
      e.preventDefault();
      this.error = "";
      if (!this.user.username) {
        this.error = "Ein Benutername muss angegeben werden";
        return;
      }
      if (!this.user.password && !this.passwordValidationError) {
        this.error = "Ein valides Passwort muss angegeben werden";
        return;
      }

      this.isLoading = true;

      // check if username already exist
      userService
        .register(this.user)
        .then(() => {
          AlertService.success("Registrierung erfolgreich");
          this.$nextTick(() => (this.isLoginDialogOpen = true));
        })
        .catch((err: Error) => (this.error = err.message || err.toString()))
        .finally(() => (this.isLoading = false));
    },
  },
});
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  justify-content: space-around;

  .section {
    flex-basis: 50%;
    @media screen and (max-width: 795px) {
      flex-basis: 100%;
    }
  }
}
</style>
